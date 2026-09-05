package com.nodephone.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.nodephone.android.data.repository.SettingsRepository
import com.nodephone.android.ui.navigation.NodePhoneNavHost
import com.nodephone.android.ui.theme.NodePhoneTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by settingsRepository.themeMode.collectAsState()

            NodePhoneTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                NodePhoneNavHost(navController = navController)
            }
        }
    }
}
