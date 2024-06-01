package com.oficialexdev.musclefire.models

import android.net.Uri
import com.google.firebase.firestore.DocumentId

data class ExerciseModel(
    @DocumentId var id: String? = null,
    val name: Int = 0,
    val trainingId: String = "",
    var image: Uri? = null,
    var observations: String = ""
)