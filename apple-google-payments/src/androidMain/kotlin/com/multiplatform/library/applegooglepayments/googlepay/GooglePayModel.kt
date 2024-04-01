package com.multiplatform.library.applegooglepayments.googlepay

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.multiplatform.library.applegooglepayments.GooglePayModel
import com.multiplatform.library.applegooglepayments.Result
import com.multiplatform.library.applegooglepayments.googlepay.utils.PaymentsUtil
import kotlinx.coroutines.tasks.await


class GooglePayModelImpl(context: Context) : GooglePayModel {
    private val paymentsClient: PaymentsClient = PaymentsUtil.createPaymentsClient(context)

    override suspend fun fetchCanUseGooglePay(): Boolean {
        val isReadyToPayJson = PaymentsUtil.isReadyToPayRequest()
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString())
        val task: Task<Boolean> = paymentsClient.isReadyToPay(request)
        return task.await()
    }

    override suspend fun getLoadPaymentDataTask(): Result<String> {
        val paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(priceCemts = 100L)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        var result: Result<String> = Result.Failure(Exception(""))
        paymentsClient.loadPaymentData(request).
            addOnCompleteListener {
                result = Result.Success(it.result.toJson())
            }.addOnFailureListener {
                result = Result.Failure(it)
            }
        return result
    }
}