plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.qrgenerator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.qrgenerator"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.journeyapps.zxing.android.embedded) // For QR code generation
    implementation (libs.core) // ZXing core library
    implementation (libs.journeyapps.zxing.android.embedded)
    implementation (libs.code.scanner)
//    implementation (libs.android.code.scanner)
}