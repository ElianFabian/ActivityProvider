[![](https://jitpack.io/v/ElianFabian/ActivityProvider.svg)](https://jitpack.io/#ElianFabian/ActivityProvider)

# ActivityProvider

A lightweight Android library that provides safe, lifecycle-aware access to the current `ComponentActivity`.

This utility manages activity references automatically via `Application.ActivityLifecycleCallbacks` to prevent memory leaks. It allows services, ViewModels, or libraries to perform operations that require an activity context without needing to pass the activity instance manually through multiple layers of code.

## Features

- **Automatic Initialization**: Uses [Jetpack Startup](https://developer.android.com/topic/libraries/app-startup) to initialize automatically.
- **Lifecycle Aware**: Automatically tracks the current activity and clears references on destruction.
- **Coroutine Support**: Suspend functions to wait for an active activity.
- **Reactive**: Observe the current activity as a `StateFlow`.
- **Reference Cleanup**: Releases the selected `ComponentActivity` when it is destroyed.

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
    implementation("com.github.ElianFabian:ActivityProvider:$version")
}
```

## Usage

The provider selects the latest `ComponentActivity` observed during creation, start, or resume.
It keeps that instance while paused or stopped and clears it when the selected instance is destroyed.
The flow reports changes of instance; it does not emit every lifecycle transition. Android operations
still need the appropriate main thread and lifecycle state. Consumers must release their own references
and registrations when they no longer need them.

With several simultaneous activities, destroying the selected instance clears the provider;
it does not automatically select an earlier instance.

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
    }
    else {
        // No activity is currently active
    }
}
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
