package com.oficialexdev.musclefire.view

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.oficialexdev.musclefire.R
import com.oficialexdev.musclefire.components.home.AppDateTimePicker
import com.oficialexdev.musclefire.components.home.AppInput
import com.oficialexdev.musclefire.models.TrainingModel
import com.oficialexdev.musclefire.viewmodel.TrainingViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun TrainingView(
    userId: String,
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    modalSheetStateCreate: SheetState,
    trainingViewModel: TrainingViewModel = TrainingViewModel(userId, db, storage)
) {
    var selectedTraining: String? by remember { mutableStateOf(null) }
    var selectedTrainingDesc: String? by remember { mutableStateOf(null) }
    var selectedTimestamp: Timestamp? by remember { mutableStateOf(null) }
    var trainingName: String by remember { mutableStateOf("") }
    var trainingDesc: String by remember { mutableStateOf("") }
    var updateId: String? by remember { mutableStateOf(null) }
    var trainingDate: Long? by remember { mutableStateOf(null) }
    var showTimePicker: Boolean by remember { mutableStateOf(false) }
    var loaded: Boolean by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState()
    var data: List<TrainingModel> by remember { mutableStateOf(listOf()) }
    trainingViewModel.data.observeForever { data = it.toList() }

    LaunchedEffect(0) {
        trainingViewModel.readAll {
            loaded = true
        }
    }

    if (modalSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                selectedTraining = null
                coroutine.launch { modalSheetState.hide() }
            },
            Modifier,
            modalSheetState
        ) {
            if (selectedTraining != null) {
                ExerciseView(
                    selectedTraining!!,
                    selectedTrainingDesc!!,
                    selectedTimestamp!!,
                    db,
                    storage
                )
            }
        }
    }

    if (modalSheetStateCreate.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutine.launch { modalSheetStateCreate.hide() }
            },
            Modifier,
            modalSheetStateCreate
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                Arrangement.Center,
                Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.training_num).replaceFirstChar { it.uppercaseChar() },
                    Modifier.padding(top = 8.dp)
                )
                AppInput(
                    value = trainingName,
                    onChange = {
                        if (it.length <= 4) {
                            trainingName = it
                        }
                    },
                    Modifier.fillMaxWidth(.85f),
                    textStyle = TextStyle(
                        textAlign = TextAlign.Center
                    ),
                    number = true
                )
                Text(
                    text = stringResource(id = R.string.training_desc).replaceFirstChar { it.uppercaseChar() },
                    Modifier.padding(top = 8.dp)
                )
                AppInput(
                    value = trainingDesc,
                    onChange = { trainingDesc = it },
                    Modifier.fillMaxWidth(.85f)
                )

                Row(
                    Modifier
                        .fillMaxWidth(.85f)
                        .padding(top = 4.dp),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    FilledIconButton(onClick = { showTimePicker = true }) {
                        Icon(imageVector = Icons.Default.Timer, contentDescription = "Time icon")
                    }
                    Button(
                        onClick = {
                            if (updateId != null) {
                                trainingViewModel.update(
                                    TrainingModel(
                                        id = updateId,
                                        name = trainingName.toInt(),
                                        description = trainingDesc,
                                        date = Timestamp(
                                            Date.from(
                                                Instant.ofEpochSecond(
                                                    trainingDate!!
                                                )
                                            )
                                        )
                                    ),
                                    data
                                )
                            } else {
                                trainingViewModel.create(
                                    trainingName.toInt(),
                                    trainingDesc,
                                    trainingDate!!,
                                    data
                                )
                            }
                            updateId = null
                            trainingDate = null
                            trainingName = ""
                            trainingDesc = ""
                            coroutine.launch {
                                modalSheetStateCreate.hide()
                            }
                        },
                        enabled = trainingDate != null && trainingDesc.isNotEmpty() && trainingName.isNotEmpty()
                    ) {
                        Text(
                            text = if (updateId != null)
                                stringResource(id = R.string.edit_training) else
                                stringResource(id = R.string.add_training)
                        )
                    }
                }
                if (trainingDate != null) {
                    Text(
                        LocalDateTime.ofInstant(
                            Instant.ofEpochSecond(trainingDate!!),
                            ZoneOffset.UTC
                        ).toString(),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize(),
        Arrangement.Center,
        Alignment.CenterHorizontally
    ) {
        if (loaded) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                Arrangement.Top,
                Alignment.CenterHorizontally
            ) {
                data.forEach {
                    var showConfirmDelete: Boolean by remember { mutableStateOf(false) }
                    Row(
                        Modifier
                            .clickable(
                                MutableInteractionSource(),
                                null
                            ) {
                                selectedTraining = it.id
                                selectedTrainingDesc = it.description
                                selectedTimestamp = it.date
                                coroutine.launch { modalSheetState.show() }
                            }
                            .fillMaxWidth()
                            .padding(8.dp),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.training).replaceFirstChar { it.uppercaseChar() }} - ${it.name}",
                            fontSize = 18.sp
                        )
                        Row {
                            FilledIconButton(onClick = {
                                updateId = it.id
                                trainingDate = it.date?.seconds
                                trainingDesc = it.description
                                trainingName = it.name.toString()
                                coroutine.launch {
                                    modalSheetStateCreate.show()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            FilledIconButton(onClick = { showConfirmDelete = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete"
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
                                            trainingViewModel.delete(it, data)
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
                    }
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }

    if (showTimePicker) {
        AppDateTimePicker(
            { showTimePicker = false }
        ) {
            trainingDate = it
        }
    }
}