package com.nodephone.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nodephone.android.feature.home.HomeScreen
import com.nodephone.android.feature.pairing.PairingScreen
import com.nodephone.android.feature.settings.SettingsScreen
import com.nodephone.android.feature.splash.SplashScreen

@Composable
fun NodePhoneNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToPairing = {
                    navController.navigate(Screen.Pairing.route)
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Pairing.route) {
            PairingScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
