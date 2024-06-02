package com.oficialexdev.musclefire.models

import com.google.firebase.firestore.DocumentId

data class ExerciseModel(
    @DocumentId var id: String? = null,
    val trainingId: String = "",
    val name: Int = 0,
    var image: String? = null,
    var observations: String = ""
)