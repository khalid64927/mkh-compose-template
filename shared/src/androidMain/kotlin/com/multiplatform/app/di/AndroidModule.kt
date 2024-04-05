package com.multiplatform.app.di

import com.multiplatform.app.data.local.datastore.DataStoreProvider
import com.multiplatform.app.data.local.DataStoreProviderImpl
import com.multiplatform.app.database.AppDatabase
import com.multiplatform.app.domain.location.AndroidLocationService
import com.multiplatform.app.domain.location.LocationService
import com.multiplatform.app.platform.PlatformSingleton
import com.multiplatform.library.applegooglepayments.GooglePayConfig
import com.multiplatform.library.applegooglepayments.SupportedMethods
import com.multiplatform.library.applegooglepayments.googlepay.GooglePayModelImpl
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dev.icerock.moko.permissions.PermissionsController
import io.ktor.client.engine.HttpClientEngine
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<DataStoreProvider> { DataStoreProviderImpl(androidContext()) }



    single<HttpClientEngine> { getEngine() }
    single<SqlDriver> {
        val googlePayConfig = GooglePayConfig(
            gateway = "example",
            gatewayMerchantId = "exampleGatewayMerchantId",
            merchantName = "MSTA",
            countryCode = "SG",
            currencyCode = "SDG",
            shippingDetails = null,
            paymentsEnvironment = 3,
            allowedCards = listOf("AMEX","DISCOVER","JCB","MASTERCARD","VISA"),
            supportedMethods = listOf(SupportedMethods.PAN_ONLY, SupportedMethods.CRYPTOGRAM_3DS)
        )
        PlatformSingleton.setPaymentInterface(GooglePayModelImpl(androidContext(), googlePayConfig))
        AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = androidContext(),
            name = "location.db",
        )
    }
    single { PermissionsController(applicationContext = androidContext())}
    factory <LocationService> { AndroidLocationService(context = androidContext()) }
}
