package com.multiplatform.library.applegooglepayments.googlepay

import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.multiplatform.library.applegooglepayments.GooglePayConfig
import com.multiplatform.library.applegooglepayments.PaymentInterface
import com.multiplatform.library.applegooglepayments.Result
import com.multiplatform.library.applegooglepayments.googlepay.utils.GooglePaymentsUtils
import com.multiplatform.library.applegooglepayments.googlepay.utils.PaymentsUtil.createPaymentsClient
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull


class GooglePayModelImpl(
    private val context: Context,
    private val config: GooglePayConfig
) : PaymentInterface {

    private val resolverFragmentTag: String = "GooglePayModel"
    private val googlePayUtils = GooglePaymentsUtils(config)
    private val paymentsClient: PaymentsClient = createPaymentsClient(context)
    private val fragmentManagerHolder = MutableStateFlow<FragmentManager?>(null)
    private val mutex: Mutex = Mutex()

    override suspend fun canMakePayments(): Boolean {
        runCatching {
            val isReadyToPayJson = googlePayUtils.isReadyToPayRequest()
            val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
            val task: Task<Boolean> = paymentsClient.isReadyToPay(request)
            return task.await()
        }.onFailure {
            return false
        }
        return true
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun makePayments(amount: String, callback: (result: Result<String>) -> Unit) {
        val paymentDataRequestJson = googlePayUtils.getPaymentDataRequest(priceCents = amount.toLong())
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        paymentsClient.loadPaymentData(request).addOnCompleteListener {
            if(it.isSuccessful) {
                callback(Result.Success(it.result.toJson()))
                return@addOnCompleteListener
            }
            if(it.exception is ResolvableApiException){
                GlobalScope.launch {
                    resolveWithPayUI(it.exception as ResolvableApiException, callback)
                }

            }

        }
    }

    private suspend fun resolveWithPayUI(resolvableApiException: ResolvableApiException, callback: (result: Result<String>) -> Unit) {
        val fragmentManager: FragmentManager = awaitFragmentManager()
        val resolverFragment: ResolverFragment = getOrCreateResolverFragment(fragmentManager)
        resolverFragment.resolvePayment(
            resolvableApiException,
            callback
        )
    }

    override fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManagerHolder.value = fragmentManager

        val observer = object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    this@GooglePayModelImpl.fragmentManagerHolder.value = null
                    source.lifecycle.removeObserver(this)
                }
            }

        }
        lifecycle.addObserver(observer)
    }

    private suspend fun awaitFragmentManager(): FragmentManager {
        val fragmentManager: FragmentManager? = fragmentManagerHolder.value
        if (fragmentManager != null) return fragmentManager

        return withTimeoutOrNull(AWAIT_FRAGMENT_MANAGER_TIMEOUT_DURATION_MS) {
            fragmentManagerHolder.filterNotNull().first()
        } ?: error(
            "fragmentManager is null, `bind` function was never called," +
                    " consider calling permissionsController.bind(lifecycle, fragmentManager)" +
                    " or BindEffect(permissionsController) in the composable function," +
                    " check the documentation for more info: " +
                    "https://github.com/icerockdev/moko-permissions/blob/master/README.md"
        )
    }

    private fun getOrCreateResolverFragment(fragmentManager: FragmentManager): ResolverFragment {
        val currentFragment: Fragment? = fragmentManager.findFragmentByTag(resolverFragmentTag)
        return if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().also { fragment ->
                fragmentManager
                    .beginTransaction()
                    .add(fragment, resolverFragmentTag)
                    .commit()
            }
        }
    }

    private companion object {
        val VERSIONS_WITHOUT_NOTIFICATION_PERMISSION =
            Build.VERSION_CODES.KITKAT until Build.VERSION_CODES.TIRAMISU
        private const val AWAIT_FRAGMENT_MANAGER_TIMEOUT_DURATION_MS = 2000L
    }
}
