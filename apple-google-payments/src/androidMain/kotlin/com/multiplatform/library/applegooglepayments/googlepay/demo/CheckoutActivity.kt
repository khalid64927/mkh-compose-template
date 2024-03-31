package com.multiplatform.library.applegooglepayments.googlepay.demo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.multiplatform.library.applegooglepayments.R


class CheckoutActivity : ComponentActivity() {

    private val googlePayRequestCode = 1001

    private val model: CheckoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val payState: PaymentUiState by model.paymentUiState.collectAsStateWithLifecycle()
            ProductScreen(
                title = "Men's Tech Shell Full-Zip",
                description = "A versatile full-zip that you can wear all day long and even...",
                price = "$50.20",
                image = R.drawable.ts_10_11019a,
                payUiState = payState,
                onGooglePayButtonClick = {
                    AutoResolveHelper.resolveTask(
                        model.getLoadPaymentDataTask(), this, googlePayRequestCode)
                },
            )
        }
    }

    @Deprecated("Deprecated and in use by Google Pay")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == googlePayRequestCode) {
            when (resultCode) {
                RESULT_OK -> data?.let { intent ->
                    PaymentData.getFromIntent(intent)?.let(model::setPaymentData)
                }
                /* Handle other result scenarios
                 * Learn more at: https://developers.google.com/pay/api/android/support/troubleshooting
                 */
                else -> { // Other uncaught errors }
                }
            }
        }
    }
}