package com.multiplatform.library.applegooglepayments

import androidx.compose.ui.platform.PlatformTextInputMethodRequest

data class ApplePayConfig(
    val amount: String,
    val productName: String,
    val merchantIdentifier: String,
    val countryCode: String,
    val currencyCode: String,
    val allowBillingAddress: Boolean,

    /**
     * ios supported values : listOf( "AMEX", "MASTERCARD", "VISA")
     * */
    val allowedCards: List<String>,
    val methodRequest: PlatformTextInputMethodRequest,
)

enum class AllowedCards {
    VISA, MASTER, AMEX
}

/**
 * https://developer.apple.com/documentation/passkit_apple_pay_and_wallet/pkmerchantcapability?language=objc
 * 3DS, DEBIT, CREDIT, EMV, INSTANT_FUNDS_OUT
 * */
enum class MerchantCapabilities {
    CapabilityInstantFundsOut, Capability3DS, CapabilityEMV, CapabilityCredit, CapabilityDebit,
}


