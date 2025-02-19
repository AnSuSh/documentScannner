package `in`.ansush.ai.documentscanner.screens

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import `in`.ansush.ai.documentscanner.AIDocScannerApp
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScannerScreen(paddingValues: PaddingValues) {

    val context = LocalContext.current

    val scanningOptions = GmsDocumentScannerOptions.Builder()
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setResultFormats(
            GmsDocumentScannerOptions.RESULT_FORMAT_PDF,
            GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
        )
        .build()

    val documentScanner = GmsDocumentScanning.getClient(scanningOptions)

    var scannedImageUris by remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    // Returns a scope that's cancelled when ScannerScreen is removed from composition
    val coroutineScope = rememberCoroutineScope()

    val scannerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scanningResult =
                    GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                scannedImageUris =
                    scanningResult?.pages?.map { page -> page.imageUri } ?: emptyList()

                scanningResult?.pdf?.let { pdfUri ->
                    // Get the private storage location to save the selected image
                    coroutineScope.launch {
                        // Use Kotlin's use function for automatic resource management
                        context.contentResolver.openInputStream(pdfUri.uri)?.use { inputStream ->

                            val storageDir = (context.applicationContext as AIDocScannerApp)
                                .getPrivateDirectory()

                            val tempImageFileToCopy = File(
                                storageDir,
                                generateFileNameBasedOnDate()
                            )

                            FileOutputStream(
                                tempImageFileToCopy
                            ).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                    }
                }
            }
        }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        val (imageContainer, scanButton) = createRefs()

        // Container for the AsyncImages
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(imageContainer) {
                    top.linkTo(parent.top)
                    bottom.linkTo(scanButton.top) // Position above the button
                }
                .verticalScroll(rememberScrollState()), // Enable scrolling if images overflow
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            scannedImageUris.forEach { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = "Scanned Image",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }

        // The "Scan PDF" button
        Button(
            onClick = {
                documentScanner.getStartScanIntent(context as Activity)
                    .addOnSuccessListener {
                        scannerLauncher.launch(
                            IntentSenderRequest.Builder(it).build()
                        )
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
            },
            modifier = Modifier.constrainAs(scanButton) {
                bottom.linkTo(parent.bottom, margin = 16.dp) // Anchor to the bottom
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
            }
        ) {
            Text(text = "Scan PDF")
        }
    }
}

fun generateFileNameBasedOnDate(): String {
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val currentDate = dateFormat.format(Date())
    return "Scan_$currentDate.pdf"
}