plugins {
    id("com.android.library") version "9.2.1"
}

android {
    namespace = "com.elianfabian.activity_provider.consumer_smoke"
    compileSdk = 35
    defaultConfig { minSdk = 21 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Deliberately no direct Activity or coroutines dependencies.
    implementation("com.github.ElianFabian:ActivityProvider:1.0.1")
}
