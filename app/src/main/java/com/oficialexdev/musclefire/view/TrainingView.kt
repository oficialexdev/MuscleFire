package com.oficialexdev.musclefire.view

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.oficialexdev.musclefire.R
import com.oficialexdev.musclefire.models.TrainingModel
import com.oficialexdev.musclefire.viewmodel.TrainingViewModel
import kotlin.random.Random

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TrainingView(
    userId: String,
    db: FirebaseFirestore,
    trainingViewModel: TrainingViewModel = TrainingViewModel(userId, db)
) {
    var loaded: Boolean by remember { mutableStateOf(false) }
    var data: List<TrainingModel> by remember { mutableStateOf(listOf()) }
    trainingViewModel.data.observeForever { data = it.toList() }

    LaunchedEffect(0) {
        trainingViewModel.readAll {
            loaded = true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        if (data.isEmpty()) Arrangement.Center else Arrangement.Top,
        Alignment.CenterHorizontally
    ) {
        if (loaded) {
            data.forEach {
                Row(
                    Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Text(text = it.description, fontSize = 18.sp)
                    Row {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = { trainingViewModel.delete(it, data) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
            Button(onClick = {
                trainingViewModel.create("New training ${Random.nextInt(0, 255)}", data)
            }) {
                Text(text = stringResource(id = R.string.add_training))
            }
        } else {
            CircularProgressIndicator(
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
    }

}