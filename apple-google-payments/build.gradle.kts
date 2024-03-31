plugins {
    alias(libs.plugins.org.jetbrains.kotlin.multiplatform.plugin)
    alias(libs.plugins.com.android.library.plugin)
    alias(libs.plugins.org.jetbrains.compose.plugin)
    alias(libs.plugins.org.jetbrains.kotlin.serialization.plugin)
    kotlin("native.cocoapods")
    id("maven-publish")
    id("signing")
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        framework {
            baseName = "apple-google-payments"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.animation)
                implementation(libs.org.jetbrains.kotlinx.coroutines.core)
                implementation(libs.touchlab.kermit)
                implementation(libs.io.insert.koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.org.jetbrains.kotlinx.coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.appcompat)
                implementation(libs.gwallet)
                implementation(libs.google.pay.button)
                implementation(libs.androidx.activity.compose)
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation(libs.com.squareup.sqldelight.native.driver)
                implementation(libs.io.ktor.client.darwin)
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.multiplatform.library.applegooglepayments"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    dependencies {
        // Google Pay
        implementation(libs.gwallet)
        implementation(libs.google.pay.button)
        implementation(libs.kotlinx.coroutines.play.services)
        implementation(libs.lifecycle.runtime.compose)

    }

}