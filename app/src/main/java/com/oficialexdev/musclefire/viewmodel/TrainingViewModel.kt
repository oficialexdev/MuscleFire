package com.oficialexdev.musclefire.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.oficialexdev.musclefire.globals.Constants
import com.oficialexdev.musclefire.globals.deleteRelatedDoc
import com.oficialexdev.musclefire.models.TrainingModel
import java.time.Instant
import java.util.Date
class TrainingViewModel(
    private val userId: String,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) :
    ViewModel() {
    var data = MutableLiveData<List<TrainingModel>>()
    fun readAll(successCallback: () -> Unit) {
        db.collection(Constants.Companion.Collections.TRAINING)
            .whereEqualTo("user", userId)
            .get()
            .addOnSuccessListener {
                successCallback.invoke()
                try {
                    val res: List<TrainingModel> = it.documents.map { doc ->
                        doc.toObject(TrainingModel::class.java)!!
                    }
                    data.value = res.toList()
//                    println(res.firstOrNull()?.date)
                } catch (e: Throwable) {
//                    println(e)
                }
            }
            .addOnFailureListener {
//                println("ERROR ON READ TRAININGS \n $it")
            }
    }
    fun create(name: Int, description: String, date: Long, dataList: List<TrainingModel>) {
        val model = TrainingModel(
            name = name,
            date = Timestamp(Date.from(Instant.ofEpochSecond(date))),
            user = userId,
            description = description
        )
        db.collection(Constants.Companion.Collections.TRAINING)
            .add(
                model
            )
            .addOnSuccessListener {
                model.id = it.id
                data.value = dataList.plus(model)
            }
            .addOnFailureListener {
//                println("ERROR\n$it")
            }
    }
    fun delete(model: TrainingModel, dataList: List<TrainingModel>) {
        db.collection(Constants.Companion.Collections.TRAINING).document("/${model.id}").delete()
            .addOnSuccessListener {
                data.value = dataList.filter {
                    it.id != model.id
                }
                db.collection(Constants.Companion.Collections.EXERCISE)
                    .whereEqualTo("trainingId", model.id).get().addOnSuccessListener {
                        it.documents.forEach { doc ->
                            storage.deleteRelatedDoc(doc.id)
                            doc.reference.delete()
                        }
                    }
            }
    }
    fun update(newModel: TrainingModel, dataList: List<TrainingModel>) {
        db.collection(Constants.Companion.Collections.TRAINING).document("/${newModel.id}")
            .update(
                "name", newModel.name,
                "description", newModel.description,
                "date", newModel.date
            ).addOnSuccessListener {
                val idx = dataList.indexOf(dataList.find { it.id == newModel.id })
                val mutable = dataList.toMutableList()
                mutable[idx] = newModel
                data.value = mutable.toList()
            }
    }
}