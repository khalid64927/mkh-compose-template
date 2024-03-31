package com.multiplatform.library.applegooglepayments

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform