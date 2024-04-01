package com.multiplatform.library.applegooglepayments.applepay

import com.multiplatform.library.applegooglepayments.ApplePayModel
import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSLog
import platform.PassKit.*
import platform.UIKit.*
import platform.darwin.NSObject

data class ApplePayConfig(
    val amount: String,
    val productName: String,
    val merchantIdentifier: String,
    val countryCode: String,
    val currencyCode: String,
    val supportedNetworks :List<*> = listOf(PKPaymentNetworkVisa, PKPaymentNetworkMasterCard, PKPaymentNetworkAmex),
    val merchantCapabilities : PKMerchantCapability)

class ApplePayModelImpl : ApplePayModel {

    override fun canMakePayments(): Boolean {
        return PKPaymentAuthorizationViewController.canMakePayments()
    }

    override fun makeApplePayment() {
        val paymentRequest = PKPaymentRequest()
        paymentRequest.merchantIdentifier = "YOUR_MERCHANT_IDENTIFIER"
        paymentRequest.countryCode = "SG"
        paymentRequest.currencyCode = "SGD"
        paymentRequest.supportedNetworks = listOf(PKPaymentNetworkVisa, PKPaymentNetworkMasterCard, PKPaymentNetworkAmex)
        paymentRequest.merchantCapabilities = PKMerchantCapability3DS

        val paymentItem = PKPaymentSummaryItem()
        paymentItem.label = "My Singtel App"
        paymentItem.amount = NSDecimalNumber("10")

        paymentRequest.paymentSummaryItems = listOf(paymentItem)

        val paymentController = PKPaymentAuthorizationViewController(paymentRequest = paymentRequest)
        paymentController.delegate = PaymentAuthorizationDelegate()

        val window = UIApplication.sharedApplication.keyWindow
        window?.rootViewController?.presentViewController(paymentController, animated = true, completion = null)

    }

}

private class PaymentAuthorizationDelegate : NSObject(), PKPaymentAuthorizationViewControllerDelegateProtocol {
    override fun paymentAuthorizationViewController(
        controller: PKPaymentAuthorizationViewController,
        didAuthorizePayment: PKPayment,
        completion: (PKPaymentAuthorizationStatus) -> Unit
    ) {
        NSLog("Payment authorized")
        // Handle payment authorization here
        completion(PKPaymentAuthorizationStatus.PKPaymentAuthorizationStatusSuccess)
    }

    override fun paymentAuthorizationViewControllerDidFinish(controller: PKPaymentAuthorizationViewController) {
        NSLog("Payment finished")
        controller.dismissViewControllerAnimated(true, completion = null)
    }
}