package `in`.ansush.ai.documentscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import `in`.ansush.ai.documentscanner.ui.theme.DocumentScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DocumentScannerTheme {
                MainScreen()
            }
        }
    }
}