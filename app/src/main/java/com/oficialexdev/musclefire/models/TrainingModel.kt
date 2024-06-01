package com.oficialexdev.musclefire.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class TrainingModel(
    @DocumentId var id: String? = null,
    val name: Int = 0,
    val user: String? = null,
    var description: String = "",
    val date: Timestamp? = null
)