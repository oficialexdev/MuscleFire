package com.oficialexdev.musclefire.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.oficialexdev.musclefire.R
import com.oficialexdev.musclefire.components.home.AppImagePicker
import com.oficialexdev.musclefire.components.home.AppInput
import com.oficialexdev.musclefire.models.ExerciseModel
import com.oficialexdev.musclefire.viewmodel.ExerciseViewModel
import kotlinx.coroutines.Dispatchers

@Composable
fun ExerciseView(
    trainingId: String,
    selectedTrainingDesc: String,
    selectedTrainingDate: Timestamp,
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    exerciseViewModel: ExerciseViewModel = ExerciseViewModel(trainingId, db, storage)
) {
    var loaded: Boolean by remember { mutableStateOf(false) }
    var showCreateExercise: Boolean by remember { mutableStateOf(false) }
    var data: List<ExerciseModel> by remember { mutableStateOf(listOf()) }
    val context = LocalContext.current
    exerciseViewModel.data.observeForever { data = it.toList() }

    LaunchedEffect(0) {
        exerciseViewModel.readAll {
            loaded = true
        }
    }


    if (loaded) {
        Column(
            Modifier
                .padding(8.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = selectedTrainingDesc)
            Text(
                text = selectedTrainingDate.toDate().toString(),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            data.filter { it.id != null }.forEach { exerciseModel ->
                var showConfirmDelete: Boolean by remember { mutableStateOf(false) }
                var tempByteArray: ByteArray? by remember { mutableStateOf(null) }
                Column {
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.exercise).replaceFirstChar { it.uppercaseChar() }} - ${exerciseModel.name}",
                            Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            fontSize = 26.sp
                        )
                        FilledIconButton(onClick = {
                            showConfirmDelete = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Icon"
                            )
                        }
                        if (showConfirmDelete) {
                            AlertDialog(
                                onDismissRequest = { showConfirmDelete = false },
                                text = {
                                    Text(text = stringResource(id = R.string.del_warning))
                                },
                                dismissButton = {
                                    Button(onClick = {
                                        showConfirmDelete = false
                                    }) {
                                        Text(
                                            text = stringResource(id = R.string.cancel).replaceFirstChar { it.uppercaseChar() },
                                        )
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = {
                                        showConfirmDelete = false
                                        exerciseViewModel.delete(
                                            exerciseModel.id!!,
                                            data
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete"
                                        )
                                        Text(
                                            text = stringResource(id = R.string.delete).replaceFirstChar { it.uppercaseChar() },
                                            Modifier.padding(start = 2.dp)
                                        )
                                    }
                                })
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (tempByteArray != null) {
                        Image(
                            bitmap = BitmapFactory.decodeByteArray(
                                tempByteArray,
                                0,
                                tempByteArray!!.size
                            ).asImageBitmap(),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp)),
                            contentDescription = "IMAGE"
                        )
                    } else if (exerciseModel.image != null) {
                        var imageLoaded: Boolean by remember { mutableStateOf(false) }
                        Box(
                            Modifier.fillMaxWidth(),
                            Alignment.Center
                        ) {
                            val img = ImageRequest.Builder(context)
                                .data(exerciseModel.image)
                                .dispatcher(Dispatchers.IO)
                                .memoryCacheKey(exerciseModel.image)
                                .diskCacheKey(exerciseModel.image)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .build()
                            AsyncImage(
                                model = img,
                                contentDescription = "User Picture",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .fillMaxWidth(),
                                onSuccess = {
                                    imageLoaded = true
                                }
                            )
                            if (!imageLoaded) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        Box(
                            Modifier.fillMaxWidth(),
                            Alignment.Center
                        ) {
                            AppImagePicker {
                                tempByteArray = it
                                exerciseViewModel.addImage(exerciseModel, data, it)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = exerciseModel.observations)
                }
            }
            Button(
                onClick = {
                    showCreateExercise = true
                },
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.add_exercise))
            }
        }
    } else {
        Box(
            Modifier
                .fillMaxWidth()
                .height(128.dp),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    if (showCreateExercise) {
        var observations: String by remember { mutableStateOf("") }
        Surface {
            Dialog(onDismissRequest = { showCreateExercise = false }) {
                Column(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        stringResource(id = R.string.exercise_obs)
                    )
                    AppInput(value = observations, onChange = { observations = it })
                    Button(
                        onClick = {
                            exerciseViewModel.create(
                                trainingId,
                                observations = observations,
                                data
                            )
                            showCreateExercise = false
                        },
                        Modifier.align(Alignment.CenterHorizontally),
                        enabled = observations.isNotEmpty()
                    ) {
                        Text(
                            stringResource(id = R.string.add_exercise)
                        )
                    }
                }
            }
        }
    }
}