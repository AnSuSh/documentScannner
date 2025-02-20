package com.ansush.ai.documentscanner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreens(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Scan : BottomBarScreens(
        route = Constants.SCAN_SCREEN,
        title = Constants.SCAN_TITLE,
        icon = Icons.Default.Search
    )

    object Scanned : BottomBarScreens(
        route = Constants.SCANNED_SCREEN,
        title = Constants.SCANNED_TITLE,
        icon = Icons.Default.Done
    )
}