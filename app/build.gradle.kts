plugins {
    id("com.android.application")
}

val signingStore = providers.gradleProperty("ONEGO_KEYSTORE_PATH").orNull
val signingStorePassword = providers.gradleProperty("ONEGO_STORE_PASSWORD").orNull
val signingKeyAlias = providers.gradleProperty("ONEGO_KEY_ALIAS").orNull
val signingKeyPassword = providers.gradleProperty("ONEGO_KEY_PASSWORD").orNull
val hasReleaseSigning = !signingStore.isNullOrBlank() &&
    !signingStorePassword.isNullOrBlank() &&
    !signingKeyAlias.isNullOrBlank() &&
    !signingKeyPassword.isNullOrBlank()

android {
    namespace = "com.onego.backload"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.onego.backload"
        minSdk = 23
        targetSdk = 36
        versionCode = providers.gradleProperty("ONEGO_VERSION_CODE").orElse("1").get().toInt()
        versionName = providers.gradleProperty("ONEGO_VERSION_NAME").orElse("1.0.0").get()
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(signingStore!!)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}
