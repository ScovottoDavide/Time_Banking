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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception
import java.lang.reflect.Type
import java.sql.Time


class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {

    var sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    private var  gson : Gson = Gson()

    //val p = clear()

    var _listServices = MutableLiveData<MutableList<TimeSlot>>().also{
        if(sharedPref.contains("listServices")){
            val type : Type = object : TypeToken<MutableList<TimeSlot>>() {}.type
            it.value = gson.fromJson(sharedPref.getString("listServices", null), type)
        }
    }

    var listServices : LiveData<MutableList<TimeSlot>> = _listServices
    fun saveServices(v : MutableList<TimeSlot>){
        _listServices.value = v
        val serialized = gson.toJson(v)
        println("SAVING")
        sharedPref.edit().putString("listServices", serialized).apply()
    }


    fun clear(){
        sharedPref.edit().clear().apply()
    }


    val currentUserAdvs : MutableLiveData<List<TimeSlot>> = MutableLiveData<List<TimeSlot>>().also { loadAdvs() }
    var currentIndexAdv : MutableLiveData<String> = MutableLiveData(String()).also { loadLastAdv() }

    private var listener1 : ListenerRegistration? = null
    private var listener2 : ListenerRegistration? = null

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
                Log.d("Prova", currentUserAdvs.value.toString())
            })
    }


    private fun loadLastAdv() {
        listener2 = FirestoreRepository().getAllAdvs().addSnapshotListener(EventListener{ value, e ->
            if(e != null){
                currentIndexAdv.value = ""
                return@EventListener
            }
            currentIndexAdv.value = value!!.documents.size.toString()
            Log.d("index", currentIndexAdv.value.toString())
        })

    }

    fun getDBTimeSlots() : LiveData<List<TimeSlot>>{
        return currentUserAdvs
    }

    fun saveAdv(value : TimeSlot) : Task<Void> {
        return FirestoreRepository().saveAdvDB(value, "Adv-"+(currentIndexAdv.value?.toInt()?.plus(1)).toString())
    }

    private fun DocumentSnapshot.toTimeSlotObject(): TimeSlot? {
        return try {
            val title = get("TITLE") as String
            val description = get("DESCRIPTION") as String
            val date = get("DATE") as String
            val time = get("TIME") as String
            val duration = get("DURATION") as String
            val location = get("LOCATION") as String
            val email = get("PUBLISHED_BY") as String
            val related_skill = get("RELATED_SKILL") as String
            val index = get("INDEX") as Long

            TimeSlot(title, description, date, time, duration, location, email, related_skill,
                index.toInt()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("prova4", e.toString())
            null
        }


    }

}








