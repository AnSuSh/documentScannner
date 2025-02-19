package `in`.ansush.ai.documentscanner.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import `in`.ansush.ai.documentscanner.BuildConfig
import java.io.File

@Composable
fun ScannedScreen(paddingValues: PaddingValues) {

    val context = LocalContext.current

    // Hold the list of scanned files in a state
    var scannedFiles by remember {
        mutableStateOf(
            context.filesDir.listFiles()?.filter { file ->
                file.name.endsWith(".pdf")
            } ?: emptyList()
        )
    }

    if (scannedFiles.isNotEmpty()) {
        // Use LazyColumn for efficient list rendering
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .padding(16.dp), // Add padding for better visual spacing
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(scannedFiles) { file ->
                ScannedFileItem(file) { isDeleted ->
                    if (isDeleted) {
                        // Refresh the list after deleting a file
                        scannedFiles = context.filesDir.listFiles()?.filter { file ->
                            file.name.endsWith(".pdf")
                        } ?: emptyList()
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No files scanned")
        }
    }
}

// Composable function for a single scanned file item
@Composable
fun ScannedFileItem(file: File, onDelete: (Boolean) -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Add padding for better visual separation
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(9f),
            overflow = TextOverflow.Ellipsis,
            text = file.name
        )
        IconButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                // Share the pdf file
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "application/pdf"
                val shareableFileUri = FileProvider.getUriForFile(
                    context,
                    BuildConfig.PROVIDER_AUTHORITY,
                    file
                )
                intent.putExtra(Intent.EXTRA_STREAM, shareableFileUri)
                context.startActivity(Intent.createChooser(intent, "Share File"))
            }) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share the File"
            )
        }
        IconButton(
            modifier = Modifier
                .weight(1f),
            onClick = {
                val isDeleted = file.delete()
                onDelete(isDeleted)
            }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete the File"
            )
        }
    }
}
