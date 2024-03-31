package com.multiplatform.library.applegooglepayments.googlepay

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.multiplatform.library.applegooglepayments.googlepay.utils.PaymentsUtil
import kotlinx.coroutines.tasks.await


interface GooglePayModel {
    suspend fun fetchCanUseGooglePay(): Boolean
    suspend fun getLoadPaymentDataTask(): Task<PaymentData>
}

class GooglePayModelImpl(context: Context) : GooglePayModel {
    private val paymentsClient: PaymentsClient = PaymentsUtil.createPaymentsClient(context)

    override suspend fun fetchCanUseGooglePay(): Boolean {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest()
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task: Task<Boolean> = paymentsClient.isReadyToPay(request)
        return task.await()
    }

    override suspend fun getLoadPaymentDataTask(): Task<PaymentData> {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCemts = 100L)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }
}