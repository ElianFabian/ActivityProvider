[![Release](https://jitpack.io/v/elianfabian/ActivityProvider.svg)](https://jitpack.io/#elianfabian/ActivityProvider)

# ActivityProvider

A lightweight Android library that provides safe, lifecycle-aware access to the current `ComponentActivity`.

This utility manages activity references automatically via `Application.ActivityLifecycleCallbacks` to prevent memory leaks. It allows services, ViewModels, or libraries to perform operations that require an activity context without needing to pass the activity instance manually through multiple layers of code.

## Features

- **Automatic Initialization**: Uses [Jetpack Startup](https://developer.android.com/topic/libraries/app-startup) to initialize automatically.
- **Lifecycle Aware**: Automatically tracks the current activity and clears references on destruction.
- **Coroutine Support**: Suspend functions to wait for an active activity.
- **Reactive**: Observe the current activity as a `StateFlow`.
- **Memory Safe**: Prevents leaks by only holding references to the active `ComponentActivity`.

## Installation

Add the JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}
```

Add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.elianfabian:ActivityProvider:1.0.0")
}
```

## Usage

### Get Current Activity (Synchronous)

Returns the current `ComponentActivity` or `null` if none are active.

```kotlin
val activity = ActivityProvider.getActivityOrNull()
```

### Wait for Activity (Asynchronous)

Suspends until a `ComponentActivity` becomes available.

```kotlin
lifecycleScope.launch {
    val activity = ActivityProvider.getActivity()
    // Use activity
}
```

### Observe Activity Changes

Observe the current activity reactively using `StateFlow`.

```kotlin
ActivityProvider.activity.collect { activity ->
    if (activity != null) {
        // Activity is active
    } else {
        // No activity is currently active
    }
}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
