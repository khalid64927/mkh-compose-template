package com.multiplatform.library.applegooglepayments

import androidx.collection.emptyFloatList


data class PaymentConfig(
    /**
     * The merchant’s two-letter ISO 3166 country code.
     * */
    val countryCode: String,
    /**
     * The three-letter ISO 4217 currency code that determines the
     * currency the payment request uses.
     * */
    val currencyCode: String,

    val allowBillingAddress: Boolean = false,

    val allowedCards: List<AllowedCards>
)

enum class AllowedCards {
    VISA,
    MASTER,
    AMEX,
    JCB, // Only supported by GooglePay
    DISCOVER
}


fun AllowedCards.toAppleCards() {

}

enum class SupportedCardMethods {
    PAN_ONLY,                   // supported only by GooglePay
    /**
     * supported by ApplePay with value Capability3DS
     * supported by GooglePay with value CRYPTOGRAM_3DS
     * */
    CRYPTOGRAM_3DS,             // supported by both Apple and Google Pay
    CapabilityInstantFundsOut,  // supported only by ApplePay
    /**
     * Chip based CC
     * */
    CapabilityEMV,              // supported only by ApplePay
    CapabilityCredit,           // supported only by ApplePay
    CapabilityDebit,            // supported only by ApplePay
}