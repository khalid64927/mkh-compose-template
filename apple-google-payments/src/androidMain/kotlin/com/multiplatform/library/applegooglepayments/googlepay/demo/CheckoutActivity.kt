package com.multiplatform.library.applegooglepayments.googlepay.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.multiplatform.library.applegooglepayments.R
import androidx.appcompat.app.AppCompatActivity
import com.multiplatform.library.applegooglepayments.onFailure
import com.multiplatform.library.applegooglepayments.onSuccess


class CheckoutActivity : AppCompatActivity() {


    private val model: CheckoutViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.googlePayModel.bind(lifecycle, fragmentManager = supportFragmentManager)
        setContent {
            val payState: PaymentUiState by model.paymentUiState.collectAsStateWithLifecycle()
            ProductScreen(
                title = "Men's Tech Shell Full-Zip",
                description = "A versatile full-zip that you can wear all day long and even...",
                price = "$50.20",
                image = R.drawable.ts_10_11019a,
                payUiState = payState,
                onGooglePayButtonClick = {
                    model.getLoadPaymentDataTask("100") { exception ->
                        exception.onSuccess {
                            println("CA : onSuccess :$it")
                        }.onFailure {
                            println("CA : onFailure :${it.message}")

                        }
                    }
                },
            )
        }
    }
}