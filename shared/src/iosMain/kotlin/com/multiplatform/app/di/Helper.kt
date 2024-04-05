package com.multiplatform.app.di

fun initKoin() {
    initKoin {
        baseLogger.d("iOS initKoin")
        modules(appModule)
    }
}
