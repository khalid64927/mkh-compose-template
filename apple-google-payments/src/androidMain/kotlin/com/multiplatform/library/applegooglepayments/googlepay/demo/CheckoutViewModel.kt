package com.multiplatform.library.applegooglepayments.googlepay.demo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.WalletConstants
import com.multiplatform.library.applegooglepayments.GooglePayConfig
import com.multiplatform.library.applegooglepayments.PaymentInterface
import com.multiplatform.library.applegooglepayments.Result
import com.multiplatform.library.applegooglepayments.SupportedMethods
import com.multiplatform.library.applegooglepayments.googlepay.GooglePayModelImpl
import com.multiplatform.library.applegooglepayments.googlepay.utils.GooglePaymentsUtils
import com.multiplatform.library.applegooglepayments.googlepay.utils.PaymentsUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONException
import org.json.JSONObject

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val _paymentUiState: MutableStateFlow<PaymentUiState> = MutableStateFlow(PaymentUiState.NotStarted)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState.asStateFlow()

    private val googlePayConfig = GooglePayConfig(
        gateway = "example",
        gatewayMerchantId = "exampleGatewayMerchantId",
        merchantName = "MSTA",
        countryCode = "SG",
        currencyCode = "SDG",
        shippingDetails = null,
        paymentsEnvironment = WalletConstants.ENVIRONMENT_TEST,
        allowedCards = listOf("AMEX","DISCOVER","JCB","MASTERCARD","VISA"),
        supportedMethods = listOf(SupportedMethods.PAN_ONLY, SupportedMethods.CRYPTOGRAM_3DS)
    )

    val googlePayModel: PaymentInterface = GooglePayModelImpl(application, googlePayConfig)

    init {
        viewModelScope.launch {
            fetchCanUseGooglePay()
        }
    }

    /**
     * Determine the user's ability to pay with a payment method supported by your app and display
     * a Google Pay payment button.
    ) */
    private suspend fun fetchCanUseGooglePay() {
        val result = googlePayModel.canMakePayments()
        val newUiState: PaymentUiState = try {
            if (result) {
                PaymentUiState.Available
            } else {
                PaymentUiState.Error(CommonStatusCodes.ERROR)
            }
        } catch (exception: ApiException) {
            PaymentUiState.Error(exception.statusCode, exception.message)
        }

        _paymentUiState.update { newUiState }
    }

    fun getLoadPaymentDataTask(amount: String, callback: (result : Result<String>) -> Unit) {
        viewModelScope.launch {

            googlePayModel.makePayments(amount,  callback)
        }

    }

    /**
     * Creates a [Task] that starts the payment process with the transaction details included.
     *
     * @return a [Task] with the payment information.
     * @see [PaymentDataRequest](https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient#loadPaymentData(com.google.android.gms.wallet.PaymentDataRequest)
    ) */
//    fun getLoadPaymentDataTask(): Task<PaymentData> {
//        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCents = 100L)
//        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
//        return paymentsClient.loadPaymentData(request)
//    }

    /**
     * At this stage, the user has already seen a popup informing them an error occurred. Normally,
     * only logging is required.
     *
     * @param statusCode will hold the value of any constant from CommonStatusCode or one of the
     * WalletConstants.ERROR_CODE_* constants.
     * @see [
     * Wallet Constants Library](https://developers.google.com/android/reference/com/google/android/gms/wallet/WalletConstants.constant-summary)
     */
    private fun handleError(statusCode: Int, message: String?) {
        Log.e("Google Pay API error", "Error code: $statusCode, Message: $message")
    }

    fun setPaymentData(paymentData: PaymentData) {
        println("PaymentData ${paymentData.toJson()}")
        val payState = extractPaymentBillingName(paymentData)?.let {
            PaymentUiState.PaymentCompleted(payerName = it)
        } ?: PaymentUiState.Error(CommonStatusCodes.INTERNAL_ERROR)

        _paymentUiState.update { payState }
    }

    private fun extractPaymentBillingName(paymentData: PaymentData): String? {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData =
                JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info")
                .getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            // Logging token string.
            Log.d(
                "Google Pay token", paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
            )

            return billingName
        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }

        return null
    }
}

abstract class PaymentUiState internal constructor(){
    object NotStarted : PaymentUiState()
    object Available : PaymentUiState()
    class PaymentCompleted(val payerName: String) : PaymentUiState()
    class Error(val code: Int, val message: String? = null) : PaymentUiState()
}