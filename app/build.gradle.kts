import org.gradle.api.internal.provider.Providers.changing

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
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
//        compose = true
        viewBinding = true
        dataBinding = true

    }
}

dependencies {
//    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))

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

    implementation("androidx.appcompat:appcompat")
    implementation("com.google.android.material:material")
    implementation("androidx.navigation:navigation-fragment-ktx")
    implementation("androidx.navigation:navigation-ui-ktx")
//
//
    implementation(platform("com.github.digio-tech:digio-bom:v1.0.73"))
//    implementation(platform("com.github.digio-tech:digio-bom:v1.0.40-beta-2"))
//
    implementation("com.github.digio-tech:gateway")
    implementation ("com.github.digio-tech:digioStateless")
    implementation("com.github.digio-tech:api_client")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout")
    implementation("com.android.volley:volley")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.google.code.gson:gson")
    implementation("com.github.digio-tech:digio_permissions")
    implementation("com.google.android.gms:play-services-location")

//    implementation("com.github.digio-tech:sdk_offlinekyc")
    implementation("org.simpleframework:simple-xml:2.7.1")
    // Latest stable version for Glide
    implementation("com.github.bumptech.glide:glide")

    implementation("com.github.digio-tech:sdk_offlinekyc")
//    implementation("org.simpleframework:simple-xml") {
//        exclude group: 'xpp3', module: 'xpp3'
//        exclude group: 'stax', module: 'stax-api'
//        exclude group: 'stax', module: 'stax'
//    }


//    implementation("com.github.digio-tech:esign_otp")
    implementation("com.github.digio-tech:protean-esign")
//    implementation 'com.github.digio-tech:protean-esign'

    implementation("com.github.digio-tech:cvl_esign")
    implementation("com.scottyab:rootbeer-lib")
    implementation("com.afollestad.material-dialogs:core") //giving duplicate class with upi
//    implementation 'com.afollestad.material-dialogs:core:3.3.0'
    implementation("com.github.digio-tech:esign_biometrics")
    implementation("com.github.digio-tech:cvl_rdservice")


    implementation("com.github.digio-tech:esign_mandate")

//    implementation("com.github.digio-tech:sdk_native_camera")
    implementation("com.github.digio-tech:image_processor")
    implementation("androidx.exifinterface:exifinterface")


    implementation("com.github.digio-tech:sdk_ml_camera")
//    implementation("com.google.mlkit:face-detection")
    implementation("com.google.android.gms:play-services-mlkit-face-detection")
    implementation("androidx.camera:camera-core")
    implementation("androidx.camera:camera-camera2")
    implementation("androidx.camera:camera-lifecycle")
    implementation("androidx.camera:camera-view")
    implementation("androidx.camera:camera-video")
    implementation("androidx.preference:preference-ktx")
    implementation("androidx.exifinterface:exifinterface")
    implementation("com.github.digio-tech:api_client")

    implementation ("com.airbnb.android:lottie:6.6.6")


    implementation("com.github.digio-tech:sdk_upi")
    implementation("androidx.core:core-ktx")
    implementation("com.squareup.retrofit2:retrofit")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("androidx.fragment:fragment-ktx")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx")
    implementation("com.squareup.retrofit2:converter-gson")
    implementation("io.coil-kt:coil:2.6.0")
//    implementation 'androidx.activity:activity:1.11.0'
//    implementation 'androidx.constraintlayout:constraintlayout:2.2.1'
    implementation("org.simpleframework:simple-xml") {
        exclude(group = "xmlpull", module = "xmlpull")
        exclude(group = "xpp3", module = "xpp3")
    }

    implementation("com.android.volley:volley") {
        exclude(group = "xmlpull", module = "xmlpull")
    }

// implementation("com.github.digio-tech:two_way_video")

//    implementation("live.videosdk:rtc-android-sdk") {
//        exclude(group = "org.json", module = "json")
//        exclude(group = "xmlpull", module = "xmlpull")
//    }
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx")
//    implementation("com.github.amitshekhariitbhu.Fast-Android-Networking:android-networking")
    implementation("com.auth0:java-jwt:4.5.1") // to generate JWT token


    implementation("com.emudhra:esign:1.0.0-SNAPSHOT") {
        isChanging = true
    }

    implementation("org.bouncycastle:bcprov-jdk16:1.45")

    // Required by eMudhra at runtime; Jitpack esign_biometrics POM doesn't propagate it
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.1") {
        exclude(group = "xpp3", module = "xpp3")
        exclude(group = "org.xmlpull", module = "xmlpull")
        exclude(group = "javax.xml.stream", module = "stax-api")
    }

}
