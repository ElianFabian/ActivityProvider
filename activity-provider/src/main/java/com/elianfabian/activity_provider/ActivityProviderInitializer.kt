package com.elianfabian.activity_provider

import android.content.Context
import android.app.Application
import androidx.startup.Initializer

class ActivityProviderInitializer : Initializer<ActivityProvider> {
    override fun create(context: Context): ActivityProvider {
        val application = context.applicationContext as Application
        ActivityProvider.init(application)
        return ActivityProvider
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
