package com.multiplatform.app.platform

interface PlatformApis {
    fun setStatusBarColor()
    fun getModelName(): String
    fun getOSVersion(): String
    fun getPlatform(): String
    fun getManufacturer(): String
}