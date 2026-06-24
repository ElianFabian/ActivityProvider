plugins {
	alias(libs.plugins.android.library)
}

android {
	namespace = "com.elianfabian.activity_provider"
	compileSdk {
		version = release(35)
	}

	defaultConfig {
		minSdk = 21

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
	}

	testOptions {
		unitTests.isIncludeAndroidResources = true
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
}

dependencies {
	implementation(libs.kotlinxCoroutinesAndroid)
	implementation(libs.androidx.appcompat)
	implementation(libs.androidx.startupRuntime)

	testImplementation(libs.junit)
	testImplementation(libs.robolectric)
	testImplementation(libs.kotlinx.coroutines.test)
	testImplementation(libs.androidx.core.ktx)
	testImplementation(libs.androidx.test.core)
}
