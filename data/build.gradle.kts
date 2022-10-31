import java.util.*

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

if (JavaVersion.current() < JavaVersion.VERSION_11) {
    throw GradleException("Please use JDK ${JavaVersion.VERSION_11} or above")
}

val secureProperties = loadCustomProperties(file("../local.properties"))

fun loadCustomProperties(file: File): java.util.Properties {
    val properties = Properties()
    if (file.isFile) {
        properties.load(file.inputStream())
    }
    return properties
}

android {
    compileSdk = libs.versions.android.sdk.compile.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.target.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        buildFeatures {
            dataBinding = true
        }

        kapt {
            correctErrorTypes = true
            useBuildCache = true

            javacOptions {
                option("-Xmaxerrs", 1000)
            }

            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", "true")
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11

        }
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    dependencies {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.coroutines.android)
        implementation(libs.androidx.core)
        implementation(libs.androidx.lifecycle.livedata)
        implementation(libs.androidx.lifecycle.viewmodel)
        implementation(libs.google.android.material)

        implementation(libs.dagger.hilt.android)
        kapt(libs.dagger.hilt.compiler)

        api(libs.okhttp.okhttp)
        api(libs.okhttp.logging)

        api(libs.retrofit.retrofit)
        implementation(libs.retrofit.converter.moshi)
        implementation(libs.retrofit.converter.scalars)

        api(libs.moshi.moshi)
        kapt(libs.moshi.codegen)

        api(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.room.sqlite)
        kapt(libs.androidx.room.compiler)

        api(libs.timber)

        testImplementation(libs.junit)
        testImplementation("androidx.room:room-testing:2.4.3")
        androidTestImplementation(libs.androidx.testing.junit)
        androidTestImplementation(libs.androidx.testing.epsresso)
        androidTestImplementation(libs.kotlinx.coroutines.test)
    }
}

group = "org.tiqr"
