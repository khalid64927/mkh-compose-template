package com.multiplatform.library.applegooglepayments

import com.multiplatform.library.applegooglepayments.applepay.ApplePayModelImpl
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    private val applePayModel: ApplePayModel =  ApplePayModelImpl()
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override fun println(message: String) {
        println(message)
    }

    override suspend fun fetchCanUseGooglePay(): Boolean {
        return false
    }

    override suspend fun getLoadPaymentDataTask(): Result<String> {
        return Result.Failure(Exception(""))
    }

    override fun canMakePayments() = applePayModel.canMakePayments()

    override fun makeApplePayment() = applePayModel.makeApplePayment()
}

actual fun getPlatform(): Platform = IOSPlatform()