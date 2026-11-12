package com.elianfabian.activity_provider.consumer_smoke

import androidx.activity.ComponentActivity
import com.elianfabian.activity_provider.ActivityProvider
import kotlinx.coroutines.flow.StateFlow

class Consumer {
    val activity: StateFlow<ComponentActivity?> = ActivityProvider.activity

    fun current(): ComponentActivity? = ActivityProvider.getActivityOrNull()

    suspend fun awaitActivity(): ComponentActivity = ActivityProvider.getActivity()
}
