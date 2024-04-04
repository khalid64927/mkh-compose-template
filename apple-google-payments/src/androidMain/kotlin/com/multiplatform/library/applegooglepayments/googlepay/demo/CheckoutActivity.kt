package com.multiplatform.library.applegooglepayments.googlepay.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.multiplatform.library.applegooglepayments.R
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.multiplatform.library.applegooglepayments.onFailure
import com.multiplatform.library.applegooglepayments.onSuccess


class CheckoutActivity : AppCompatActivity() {

    private val googlePayRequestCode = 1001

    // Handle potential conflict from calling loadPaymentData.
    private val resolvePaymentForResult = registerForActivityResult(StartIntentSenderForResult()) {
            result: ActivityResult ->
        when (result.resultCode) {
            RESULT_OK ->
                result.data?.let { intent ->
                    //PaymentData.getFromIntent(intent)?.let(::handlePaymentSuccess)
                }

            RESULT_CANCELED -> {
                // The user cancelled the payment attempt
            }
        }
    }

    private val model: CheckoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.googlePayModel.bind(lifecycle, fragmentManager = supportFragmentManager)
        setContent {
            val payState: PaymentUiState by model.paymentUiState.collectAsStateWithLifecycle()
            ProductScreen(
                title = "Men's Tech Shell Full-Zip",
                description = "A versatile full-zip that you can wear all day long and even...",
                price = "$50.20",
                image = R.drawable.ts_10_11019a,
                payUiState = payState,
                onGooglePayButtonClick = {
                    model.getLoadPaymentDataTask("100") { exception ->
                        exception.onSuccess {
                            println("CA : onSuccess :$it")
                        }.onFailure {
                            when(it){
                                is ResolvableApiException -> {
                                    println("CA : ResolvableApiException :${it.message}")
                                    resolvePaymentForResult.launch(
                                    IntentSenderRequest.Builder(it.resolution).build())

                                }
                            }
                        }
                    }
                },
            )
        }
    }

    @Deprecated("Deprecated and in use by Google Pay")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googlePayRequestCode) {
            when (resultCode) {
                RESULT_OK -> data?.let { intent ->
                    PaymentData.getFromIntent(intent)?.let(model::setPaymentData)
                }
                /* Handle other result scenarios
                 * Learn more at: https://developers.google.com/pay/api/android/support/troubleshooting
                 */
                else -> { // Other uncaught errors }
                }
            }
        }
    }
}