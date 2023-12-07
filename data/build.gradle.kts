import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp")
    kotlin("kapt")
}

if (JavaVersion.current() < JavaVersion.VERSION_11) {
    throw GradleException("Please use JDK ${JavaVersion.VERSION_11} or above")
}

val secureProperties = loadCustomProperties(file("../local.properties"))

fun loadCustomProperties(file: File): Properties {
    val properties = Properties()
    if (file.isFile) {
        properties.load(file.inputStream())
    }
    return properties
}

android {
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

        }
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    kotlin {
        jvmToolchain(17)
    }

    namespace = "org.tiqr.data"
    buildFeatures {
        buildConfig = true
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
        ksp(libs.dagger.hilt.compiler)

        api(libs.okhttp.okhttp)
        api(libs.okhttp.logging)

        api(libs.retrofit.retrofit)
        implementation(libs.retrofit.converter.moshi)
        implementation(libs.retrofit.converter.scalars)

        api(libs.moshi.moshi)
        ksp(libs.moshi.codegen)

        api(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.room.sqlite)
        ksp(libs.androidx.room.compiler)

        api(libs.timber)

        testImplementation(libs.junit)
        testImplementation(libs.androidx.room.testing)
        androidTestImplementation(libs.androidx.testing.junit)
        androidTestImplementation(libs.androidx.testing.epsresso)
        androidTestImplementation(libs.kotlinx.coroutines.test)
    }
}

group = "org.tiqr"
