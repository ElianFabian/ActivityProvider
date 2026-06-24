package com.elianfabian.activity_provider

import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ActivityProviderTest {

    @Before
    fun setup() {
        ActivityProvider.reset()
        ActivityProvider.init(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun `initial activity is null`() {
        assertNull(ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `onActivityCreated updates current activity`() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create()
        
        assertEquals(controller.get(), ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `onActivityStarted updates current activity`() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create().start()
        
        assertEquals(controller.get(), ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `onActivityResumed updates current activity`() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create().start().resume()
        
        assertEquals(controller.get(), ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `onActivityDestroyed clears current activity`() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create().start().resume()
        
        assertEquals(controller.get(), ActivityProvider.getActivityOrNull())
        
        controller.pause().stop().destroy()
        
        assertNull(ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `getActivity suspends until activity is available`() = runTest {
        val deferredActivity = async { ActivityProvider.getActivity() }
        
        assertNull(ActivityProvider.getActivityOrNull())
        
        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create()
        
        val activity = deferredActivity.await()
        assertEquals(controller.get(), activity)
        assertEquals(controller.get(), ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `activity StateFlow emits updates`() = runTest {
        val activities = mutableListOf<ComponentActivity?>()
        val job = launch(UnconfinedTestDispatcher()) {
            ActivityProvider.activity.collect {
                activities.add(it)
            }
        }

        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create()
        controller.pause().stop().destroy()

        job.cancel()

        // Emissions: initial null, created activity, destroyed null
        assertEquals(3, activities.size)
        assertNull(activities[0])
        assertEquals(controller.get(), activities[1])
        assertNull(activities[2])
    }
}
