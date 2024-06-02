package com.oficialexdev.musclefire.globals

import com.google.firebase.storage.FirebaseStorage

fun FirebaseStorage.deleteRelatedDoc(relatedId: String) {
    this.getReference(relatedId).delete()
}