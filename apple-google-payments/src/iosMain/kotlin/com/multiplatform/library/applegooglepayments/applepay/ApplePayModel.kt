package com.multiplatform.library.applegooglepayments.applepay

import com.multiplatform.library.applegooglepayments.ApplePayConfig
import com.multiplatform.library.applegooglepayments.PaymentInterface
import com.multiplatform.library.applegooglepayments.Result
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSLog
import platform.PassKit.*
import platform.UIKit.*
import platform.darwin.NSObject

data class ApplePayConfig(
    val productName: String,
    val merchantIdentifier: String,
    val countryCode: String,
    val currencyCode: String,
    val supportedNetworks :List<*> = listOf(PKPaymentNetworkVisa, PKPaymentNetworkMasterCard, PKPaymentNetworkAmex),
    val merchantCapabilities : PKMerchantCapability)

class ApplePayModelImpl : PaymentInterface {
    private var config: ApplePayConfig? = null

    override suspend fun canMakePayments(): Boolean {
        return PKPaymentAuthorizationViewController.canMakePayments()
    }

    override suspend fun makePayments(amount: String, callback: (result: Result<String>) -> Unit) {
        val paymentRequest = PKPaymentRequest()
        paymentRequest.merchantIdentifier = "YOUR_MERCHANT_IDENTIFIER"
        paymentRequest.countryCode = "SG"
        paymentRequest.currencyCode = "SGD"
        paymentRequest.supportedNetworks = listOf(
            PKPaymentNetworkVisa,
            PKPaymentNetworkMasterCard,
            PKPaymentNetworkAmex,
            PKPaymentNetworkDiscover)
        paymentRequest.merchantCapabilities = PKMerchantCapability3DS

        val paymentItem = PKPaymentSummaryItem()
        paymentItem.label = "My Singtel App"
        paymentItem.amount = NSDecimalNumber("10")

        paymentRequest.paymentSummaryItems = listOf(paymentItem)

        val paymentController = PKPaymentAuthorizationViewController(paymentRequest = paymentRequest)
        paymentController.delegate = PaymentAuthorizationDelegate(callback)

        val window = UIApplication.sharedApplication.keyWindow
        window?.rootViewController?.presentViewController(paymentController, animated = true, completion = null)

    }

}

private class PaymentAuthorizationDelegate(private val callback: (result: Result<String>) -> Unit) : NSObject(), PKPaymentAuthorizationViewControllerDelegateProtocol {
    override fun paymentAuthorizationViewController(
        controller: PKPaymentAuthorizationViewController,
        didAuthorizePayment: PKPayment,
        completion: (PKPaymentAuthorizationStatus) -> Unit
    ) {
        NSLog("Payment authorized")
        // Handle payment authorization here
        completion(PKPaymentAuthorizationStatus.PKPaymentAuthorizationStatusSuccess)
        callback(Result.Success(PKPaymentAuthorizationStatus.PKPaymentAuthorizationStatusSuccess.name))

    }

    override fun paymentAuthorizationViewControllerDidFinish(controller: PKPaymentAuthorizationViewController) {
        NSLog("Payment finished")
        controller.dismissViewControllerAnimated(true, completion = null)
    }
}