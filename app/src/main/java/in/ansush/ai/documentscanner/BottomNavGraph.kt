package `in`.ansush.ai.documentscanner

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.ansush.ai.documentscanner.screens.ScannedScreen
import `in`.ansush.ai.documentscanner.screens.ScannerScreen

@Composable
fun BottomNavGraph(
    paddingValues: PaddingValues,
    navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreens.Scan.route
    ) {
        composable(BottomBarScreens.Scan.route) {
            ScannerScreen(paddingValues)
        }
        composable(BottomBarScreens.Scanned.route) {
            ScannedScreen(paddingValues)
        }
    }
}