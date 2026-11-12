package com.elianfabian.activity_provider

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

/**
 * Provides the latest [ComponentActivity] observed during creation, start, or resume.
 *
 * This utility manages activity references automatically via [Application.ActivityLifecycleCallbacks]
 * and releases the selected instance when it is destroyed. It allows services,
 * view models, or libraries to perform operations that require an activity context without
 * needing to pass the activity instance manually through layers of code.
 */
object ActivityProvider {

	private val _currentActivity = MutableStateFlow<ComponentActivity?>(null)

	internal fun reset() {
		_currentActivity.value = null
	}

	/**
	 * The selected instance, retained while paused or stopped and cleared on destruction.
	 * This represents instance changes, not individual lifecycle events. Activity operations
	 * must still respect Android's main-thread and lifecycle requirements.
	 */
	val activity: StateFlow<ComponentActivity?> = _currentActivity.asStateFlow()

	/**
	 * Gets the selected [ComponentActivity], or null if no instance is selected.
	 */
	fun getActivityOrNull(): ComponentActivity? = _currentActivity.value

	/**
	 * Suspends execution until a [ComponentActivity] is available.
	 */
	suspend fun getActivity(): ComponentActivity {
		return _currentActivity.filterNotNull().first()
	}


	internal fun init(application: Application) {
		application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				handleActivityLifecycle(activity)
			}

			override fun onActivityStarted(activity: Activity) {
				handleActivityLifecycle(activity)
			}

			override fun onActivityResumed(activity: Activity) {
				handleActivityLifecycle(activity)
			}

			override fun onActivityPaused(activity: Activity) {}

			override fun onActivityStopped(activity: Activity) {}

			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

			override fun onActivityDestroyed(activity: Activity) {
				if (_currentActivity.value === activity) {
					_currentActivity.value = null
				}
			}
		})
	}

	private fun handleActivityLifecycle(activity: Activity) {
		if (activity is ComponentActivity) {
			_currentActivity.value = activity
		}
	}
}
