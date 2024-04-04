package com.multiplatform.library.applegooglepayments



enum class AllowedCards {
    VISA,
    MASTER,
    AMEX,
    JCB, // Only supported by GooglePay
    DISCOVER
}

enum class CountryCode {
    SG, US, GB,
}

enum class CurrencyCode {
    SGD, USD, GBP
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