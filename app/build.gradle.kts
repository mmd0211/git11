plugins {
    id("com.android.application")
}

android {
    namespace = "ir.sigmarobotic.sigmaplanner"
    compileSdk = 35

    defaultConfig {
        applicationId = "ir.sigmarobotic.sigmaclassplanner"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
