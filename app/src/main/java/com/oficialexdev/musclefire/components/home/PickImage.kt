package com.oficialexdev.musclefire.components.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun PickImage(saveImage: (bytes: ByteArray) -> Unit) {
    val context = LocalContext.current
    var showPhotoPicker: Boolean by remember { mutableStateOf(false) }

    IconButton(onClick = { showPhotoPicker = true }) {
        Icon(Icons.Default.Image, "")
    }

    if (showPhotoPicker) {
        val pickPhoto =
            rememberLauncherForActivityResult(
                ActivityResultContracts.PickVisualMedia()
            ) {
                if (it != null) {
                    val inputStream = context.contentResolver.openInputStream(it)
                    if (inputStream != null) {
                        val bytes = inputStream.readBytes()
                        //LIMITING VALUE TO EQUAL OR LESS THAN 5MB
                        if (bytes.size / 1048576 <= 5) {
                            Toast.makeText(context, "Uploading file ...", Toast.LENGTH_SHORT).show()
                            saveImage(bytes)
                        } else {
                            Toast.makeText(context, "File too large", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                showPhotoPicker = false
            }
        SideEffect {
            pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}