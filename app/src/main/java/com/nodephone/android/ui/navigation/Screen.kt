package com.nodephone.android.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object Pairing : Screen("pairing")
    data object TrustedDevices : Screen("trusted-devices")
    data object Projects : Screen("projects")
}
