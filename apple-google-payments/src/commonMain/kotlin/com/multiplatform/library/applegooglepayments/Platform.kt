package com.multiplatform.library.applegooglepayments

interface Platform: GooglePayModel, ApplePayModel {
    val name: String
    fun println(message: String)

}

expect fun getPlatform(): Platform

interface GooglePayModel {
    suspend fun fetchCanUseGooglePay(): Boolean
    suspend fun getLoadPaymentDataTask(): Result<String>
}

interface ApplePayModel {
    fun canMakePayments(): Boolean
    // TODO
    fun makeApplePayment()
}


