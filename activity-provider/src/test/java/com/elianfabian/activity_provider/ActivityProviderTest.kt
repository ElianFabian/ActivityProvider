package com.elianfabian.activity_provider

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21, 35])
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
        runCurrent()
        assertFalse(deferredActivity.isCompleted)
        
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
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            ActivityProvider.activity.collect {
                activities.add(it)
            }
        }

        val controller = Robolectric.buildActivity(ComponentActivity::class.java)
        controller.create().start().resume()
        controller.pause().stop().destroy()

        job.cancel()

        // Emissions: initial null, created activity, destroyed null
        assertEquals(3, activities.size)
        assertNull(activities[0])
        assertEquals(controller.get(), activities[1])
        assertNull(activities[2])
    }

    @Test
    fun `waiting for an activity is cancellable`() = runTest {
        val waiting = async { ActivityProvider.getActivity() }
        runCurrent()
        assertFalse(waiting.isCompleted)
        waiting.cancelAndJoin()
        assertTrue(waiting.isCancelled)
        val controller = Robolectric.buildActivity(ComponentActivity::class.java).create().start().resume()
        assertSame(controller.get(), ActivityProvider.getActivity())
        controller.pause().stop().destroy()
    }

    @Test
    fun `paused and stopped instance remains available`() = runTest {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java).create().start().resume()
        controller.pause().stop()
        assertSame(controller.get(), ActivityProvider.getActivityOrNull())
        assertSame(controller.get(), ActivityProvider.getActivity())
        controller.destroy()
        assertNull(ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `destroying previous instance preserves latest activity`() {
        val first = Robolectric.buildActivity(ComponentActivity::class.java).create().start().resume()
        val second = Robolectric.buildActivity(ComponentActivity::class.java).create().start().resume()
        first.pause().stop().destroy()
        assertSame(second.get(), ActivityProvider.getActivityOrNull())
        second.pause().stop().destroy()
        assertNull(ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `recreation replaces activity reference`() {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java).create().start().resume()
        val previous = controller.get()
        controller.recreate()
        assertNotSame(previous, ActivityProvider.getActivityOrNull())
        assertSame(controller.get(), ActivityProvider.getActivityOrNull())
        controller.pause().stop().destroy()
        assertNull(ActivityProvider.getActivityOrNull())
    }

    @Test
    fun `plain Activity does not replace selected ComponentActivity`() {
        val selected = Robolectric.buildActivity(ComponentActivity::class.java).create().start().resume()
        val plain = Robolectric.buildActivity(Activity::class.java).create().start().resume()
        assertSame(selected.get(), ActivityProvider.getActivityOrNull())
        plain.pause().stop().destroy()
        assertSame(selected.get(), ActivityProvider.getActivityOrNull())
        selected.pause().stop().destroy()
    }
}
