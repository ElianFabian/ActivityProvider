package com.elianfabian.activityprovider

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.elianfabian.activity_provider.ActivityProvider
import com.elianfabian.activityprovider.ui.theme.ActivityProviderTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ActivityLogger {
	private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	init {
		scope.launch {
			ActivityProvider.activity.collect { activity ->
				println("$$$ Global Observation -> activity: ${activity?.javaClass?.simpleName} (Hash: ${activity?.hashCode()})")
			}
		}
	}

	// Empty method to force object initialization
	fun watch() {}
}

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		ActivityLogger.watch()

		enableEdgeToEdge()
		setContent {
			ActivityProviderTheme {
				Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
					Box(
						contentAlignment = Alignment.Center,
						modifier = Modifier
							.fillMaxSize()
							.padding(innerPadding),
					) {
						Text(text = "Check your Logcat for ActivityProvider logs!")
					}
				}
			}
		}
	}
}
