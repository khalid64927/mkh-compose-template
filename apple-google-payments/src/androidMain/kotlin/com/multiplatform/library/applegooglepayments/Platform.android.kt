package com.multiplatform.library.applegooglepayments

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"

    override fun println(message: String) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchCanUseGooglePay(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getLoadPaymentDataTask(): Result<String> {
        TODO("Not yet implemented")
    }

    override fun canMakePayments(): Boolean {
        TODO("Not yet implemented")
    }

    override fun makeApplePayment() {
        TODO("Not yet implemented")
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()