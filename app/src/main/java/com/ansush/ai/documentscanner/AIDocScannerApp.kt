package com.ansush.ai.documentscanner

import android.app.Application
import android.os.Build
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AIDocScannerApp : Application() {

    suspend fun getPrivateDirectory(): File? {
        return withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return@withContext this@AIDocScannerApp.filesDir // Access app's internal storage
            } else {
                return@withContext this@AIDocScannerApp.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) // App's private storage
            }
        }
    }
}