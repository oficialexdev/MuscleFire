package com.oficialexdev.musclefire.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.oficialexdev.musclefire.globals.Constants
import com.oficialexdev.musclefire.globals.deleteRelatedDoc
import com.oficialexdev.musclefire.models.ExerciseModel

class ExerciseViewModel(
    private val trainingId: String,
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) :
    ViewModel() {
    var data = MutableLiveData<List<ExerciseModel>>()
    fun readAll(successCallback: () -> Unit) {
        db.collection(Constants.Companion.Collections.EXERCISE)
            .whereEqualTo("trainingId", trainingId)
            .get()
            .addOnSuccessListener {
                successCallback.invoke()
                try {
                    val res: List<ExerciseModel> = it.documents.map { doc ->
                        doc.toObject(ExerciseModel::class.java)!!
                    }
                    data.value = res.toList()
//                    println(res)
                } catch (e: Throwable) {
//                    println(e)
                }
            }
            .addOnFailureListener {
//                println("ERROR ON READ EXERCISES \n $it")
            }
    }

    fun create(trainingId: String, observations: String, dataList: List<ExerciseModel>) {
        val lastId = dataList.maxByOrNull { it.name }?.name ?: 0
        val model = ExerciseModel(
            trainingId = trainingId,
            observations = observations,
            name = lastId + 1
        )
        db.collection(Constants.Companion.Collections.EXERCISE)
            .add(model)
            .addOnSuccessListener {
                model.id = it.id
                data.value = dataList.plus(model)
            }
            .addOnFailureListener {
//                println("ERROR\n$it")
            }
    }

    fun delete(
        modelId: String,
        dataList: List<ExerciseModel>
    ) {
        db.collection(Constants.Companion.Collections.EXERCISE)
            .document("/$modelId")
            .delete()
            .addOnSuccessListener {
                data.value = dataList.filter { it.id != modelId }
                storage.deleteRelatedDoc(modelId)
            }
    }

    private fun update(newModel: ExerciseModel, dataList: List<ExerciseModel>) {
        db.collection(Constants.Companion.Collections.EXERCISE).document("/${newModel.id}")
            .update(
                "image", newModel.image
            ).addOnSuccessListener {
//                println(newModel)
                val idx = dataList.indexOf(dataList.find { it.id == newModel.id })
                val mutable = dataList.toMutableList()
                mutable[idx] = newModel
                data.value = mutable.toList()
            }.addOnFailureListener {
//                println(it)
            }
    }

    fun addImage(model: ExerciseModel, dataList: List<ExerciseModel>, bytes: ByteArray) {
        storage.getReference("/${model.id}")
            .putBytes(bytes)
            .addOnSuccessListener {
                storage.getReference("/${model.id}").downloadUrl.addOnSuccessListener {
                    model.image = it.toString()
                    update(model, dataList)
                }
            }
    }
}