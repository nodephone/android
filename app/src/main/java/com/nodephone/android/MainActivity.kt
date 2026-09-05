package com.nodephone.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.nodephone.android.ui.navigation.NodePhoneNavHost
import com.nodephone.android.ui.theme.NodePhoneTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NodePhoneTheme {
                val navController = rememberNavController()
                NodePhoneNavHost(navController = navController)
            }
        }
    }
}
