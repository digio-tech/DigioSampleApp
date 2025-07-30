plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.test.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.test.myapplication"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
//        compose = true
        viewBinding = true
        dataBinding = true

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // digio's
    implementation(platform("com.github.digio-tech:digio-bom:v1.0.17"))
    implementation("com.github.digio-tech:gateway")

    implementation("androidx.appcompat:appcompat")
    implementation("com.google.android.material:material")
    implementation("androidx.navigation:navigation-fragment-ktx")
    implementation("androidx.navigation:navigation-ui-ktx")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout")
    implementation("com.android.volley:volley")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.google.code.gson:gson")
    implementation("com.github.digio-tech:digio_permissions")
    implementation("com.google.android.gms:play-services-location")

    implementation("com.github.digio-tech:sdk_offlinekyc")
    implementation("com.github.digio-tech:protean-esign")

    implementation("com.github.digio-tech:cvl_esign")
    implementation("com.scottyab:rootbeer-lib")
    implementation("com.afollestad.material-dialogs:core")

    implementation("com.github.digio-tech:esign_biometrics")
    implementation("com.github.digio-tech:cvl_rdservice")

    implementation("com.github.digio-tech:esign_mandate")

    implementation("com.github.digio-tech:sdk_native_camera")
    implementation("com.github.digio-tech:image_processor")
    implementation("androidx.exifinterface:exifinterface")

    implementation("com.github.digio-tech:sdk_ml_camera")
    implementation("com.google.mlkit:face-detection")
    implementation("androidx.camera:camera-core")
    implementation("androidx.camera:camera-camera2")
    implementation("androidx.camera:camera-lifecycle")
    implementation("androidx.camera:camera-view")
    implementation("androidx.camera:camera-video")
    implementation("androidx.preference:preference-ktx")

    implementation("com.github.digio-tech:sdk_upi")
    implementation("androidx.core:core-ktx")
    implementation("com.squareup.retrofit2:retrofit")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("androidx.fragment:fragment-ktx")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx")
    implementation("com.squareup.retrofit2:converter-gson")

}
