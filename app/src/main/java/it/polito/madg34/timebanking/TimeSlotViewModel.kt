package it.polito.madg34.timebanking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import java.lang.reflect.Type
import java.sql.Time


class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    val currentUserAdvs: MutableLiveData<List<TimeSlot>> = MutableLiveData<List<TimeSlot>>().also { loadAdvs() }
    var currentIndexAdv: MutableLiveData<String> = MutableLiveData(String()).also { loadLastAdv() }

    var currentShownAdv : TimeSlot? = null
    var userUri = ""
    var filtered : MutableLiveData<Boolean> = MutableLiveData(false)
    var filteredTimeSlots : MutableList<TimeSlot> = mutableListOf()

    private var listener1: ListenerRegistration? = null
    private var listener2: ListenerRegistration? = null

    fun loadAdvs() {
        listener1 = FirestoreRepository().getAdvs()
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    currentUserAdvs.value = emptyList()
                    return@EventListener
                }
                currentUserAdvs.value = value!!.mapNotNull { d ->
                    d.toTimeSlotObject()
                }
            })
    }

    private fun loadLastAdv() {
        listener2 =
            FirestoreRepository().getAllAdvs().addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    currentIndexAdv.value = ""
                    return@EventListener
                }
                if (value!!.documents.size > 0){
                    var max = 0
                    value.documents.forEach {
                        if(it.id.split("-")[1].toInt() > max)
                            max = it.id.split("-")[1].toInt()
                    }
                    currentIndexAdv.value = "Adv-$max"
                }
                else currentIndexAdv.value = "Adv-0"
            })

    }

    fun getDBTimeSlots(): LiveData<List<TimeSlot>> {
        return currentUserAdvs
    }

    fun saveAdv(value: TimeSlot): Task<Void> {
        val id1 = currentIndexAdv.value?.split("-")?.get(1)?.toInt()
        val id2 = "Adv-" + (id1!! + 1).toString()
        value.id = id2
        return FirestoreRepository().saveAdvDB(value).addOnCompleteListener { loadLastAdv() }
    }

    fun updateAdv(value: TimeSlot): Task<Void> {
        return FirestoreRepository().updateAdvDB(value)
    }

    fun removeAdv(value: TimeSlot): Task<Void> {
        return FirestoreRepository().removeAdvDB(value)
    }

    fun getImageFromEmail(email : String) : Task<DocumentSnapshot> {
        return FirestoreRepository().getUserFromEmail(email).get().addOnSuccessListener { res ->
            if (res != null) {
                userUri = res.getString("uri").toString()
            }
        }
    }

    private fun DocumentSnapshot.toTimeSlotObject(): TimeSlot? {
        return try {
            val id = get("ID") as String
            val title = get("TITLE") as String
            val description = get("DESCRIPTION") as String
            val date = get("DATE") as String
            val time = get("TIME") as String
            val duration = get("DURATION") as String
            val location = get("LOCATION") as String
            val email = get("PUBLISHED_BY") as String
            val related_skill = get("RELATED_SKILL") as String
            val index = get("INDEX") as Long

            TimeSlot(
                id, title, description, date, time, duration, location, email, related_skill,
                index.toInt()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("prova4", e.toString())
            null
        }
    }

}








