package com.multiplatform.app.platform

import com.multiplatform.app.di.baseLogger
import com.multiplatform.library.applegooglepayments.PaymentInterface

object PlatformSingleton {
    private var paymentInterface: PaymentInterface? = null

    fun setPaymentInterface(paymentInterface: PaymentInterface){
        baseLogger.d( " setPaymentInterface ")
        this.paymentInterface = paymentInterface
    }

    fun getPaymentInterface(): PaymentInterface? {
        if(paymentInterface == null){
            baseLogger.d( " paymentInterface is null ")
        } else {
            baseLogger.d( " paymentInterface is not null ")
        }
        return paymentInterface
    }


}