package com.multiplatform.library.applegooglepayments

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect interface PaymentInterface {
    suspend fun canMakePayments(): Boolean
    suspend fun makePayments(amount: String, callback: (result: Result<String>) -> Unit)
}



