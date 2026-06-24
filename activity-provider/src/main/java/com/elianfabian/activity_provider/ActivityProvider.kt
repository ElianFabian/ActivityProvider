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
 * A singleton that provides safe, lifecycle-aware access to the current [ComponentActivity].
 *
 * This utility manages activity references automatically via [Application.ActivityLifecycleCallbacks]
 * to prevent common memory leaks associated with static references. It allows services,
 * view models, or libraries to perform operations that require an activity context without
 * needing to pass the activity instance manually through layers of code.
 */
object ActivityProvider {

	private val _currentActivity = MutableStateFlow<ComponentActivity?>(null)

	internal fun reset() {
		_currentActivity.value = null
	}

	/**
	 * A reactive stream of the current [ComponentActivity]. Emits null when no activity is active.
	 */
	val activity: StateFlow<ComponentActivity?> = _currentActivity.asStateFlow()

	/**
	 * Get the current [ComponentActivity] or null if there is none.
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
