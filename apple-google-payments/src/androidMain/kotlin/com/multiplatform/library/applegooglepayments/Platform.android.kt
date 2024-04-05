package com.multiplatform.library.applegooglepayments

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.multiplatform.library.applegooglepayments.googlepay.GooglePayModelImpl

class AndroidPlatform() : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual interface PaymentInterface {
    actual suspend fun canMakePayments(): Boolean
    actual suspend fun makePayments(
        amount: String,
        callback: (result: Result<String>) -> Unit
    )

    fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager)

    companion object {
        operator fun invoke(
            context: Context,
            config: GooglePayConfig
        ): PaymentInterface {
            return GooglePayModelImpl(context, config)
        }
    }
}