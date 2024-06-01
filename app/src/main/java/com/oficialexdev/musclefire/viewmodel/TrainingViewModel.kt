package com.oficialexdev.musclefire.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.oficialexdev.musclefire.globals.Constants
import com.oficialexdev.musclefire.models.TrainingModel

class TrainingViewModel(private val userId: String, private val db: FirebaseFirestore) :
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
                } catch (e: Throwable) {
//                    println(e)
                }
            }
            .addOnFailureListener {
//                println("SOME ERROR \n $it")
            }
    }


    fun create(description: String, dataList: List<TrainingModel>) {
        val model = TrainingModel(
            name = 1,
            //TODO THAT IS FOR DEFINE TIME NOW OR USING TIMER PICKER ?
            date = Timestamp.now(),
            user = userId,
            description = description
        )

        db.collection(Constants.Companion.Collections.TRAINING)
            .add(
                model
            )
            .addOnSuccessListener {
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
                            doc.reference.delete()
                        }
                    }
            }
    }

    //TODO TEST AND IMPLEMENT
    fun update(model: TrainingModel, newModel: TrainingModel, dataList: List<TrainingModel>) {
        db.collection(Constants.Companion.Collections.TRAINING).document("/${model.id}")
            .update(
                "name", newModel.name,
                "description", newModel.description,
                "date", newModel.date
            ).addOnSuccessListener {
                val idx = dataList.indexOf(model)
                val mutable = dataList.toMutableList()
                mutable[idx] = newModel
                data.value = mutable.toList()
            }
    }

}