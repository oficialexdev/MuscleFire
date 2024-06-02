package com.oficialexdev.musclefire.components.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.oficialexdev.musclefire.R
@Composable
fun AppImagePicker(saveImage: (bytes: ByteArray) -> Unit) {
    val context = LocalContext.current
    var showPhotoPicker: Boolean by remember { mutableStateOf(false) }

    Button(onClick = { showPhotoPicker = true }) {
        Icon(Icons.Default.Image, "")
        Text(text = stringResource(id = R.string.add_image), Modifier.padding(start = 2.dp))
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