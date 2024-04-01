package com.multiplatform.library.applegooglepayments

data class GooglePayConfig(
    val amount: String,
    /**
     * sample : example
     * */
    val gateway: String,
    /**
     * sample : exampleGatewayMerchantId
     * */
    val gatewayMerchantId: String,
    val merchantName: String,
    /**
     * sample : SG, US, GB
     * */
    val countryCode: String,
    /**
     * sample : SGD, USD, GBP
     * */
    val currencyCode: String,

    val shippingDetails: ShippingDetails?,
    /**
     * sample : listOf( "AMEX", "DISCOVER", "JCB", "MASTERCARD", "VISA")
     * */
    val allowedCards: List<String>,
    val supportedMethods: List<SupportedMethods>,
)

data class ShippingDetails(
    /**
     * sample: listOf("US", "GB", "SG")
     * **/
    val shippingCountryCodeList: List<String>)

enum class SupportedMethods {
    PAN_ONLY, CRYPTOGRAM_3DS
}

enum class GPAY_ENV {
    TEST, PROD
}

