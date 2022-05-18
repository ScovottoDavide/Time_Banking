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


   /* val _listServices =
        MutableLiveData<List<TimeSlot>?>() //by lazy { MutableLiveData<List<TimeSlot>>().also { loadList() } }
    val listServices: LiveData<List<TimeSlot>?> = _listServices
    private var fireStoreDB = FirebaseFirestore.getInstance()

    companion object {
        // Current Authenticated user
        lateinit var currentUser: FirebaseUser
    }

    private var listener2: ListenerRegistration? = null


    init {
        listener2 = fireStoreDB.collection("listOfservice")
            .whereEqualTo("email", FirestoreRepository.currentUser.email!!)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    _listServices.value = null
                    return@EventListener
                }
                _listServices.value = value!!.mapNotNull { d ->
                    d.toTimeSlotObject()
                }

                Log.d("Prova", _listServices.value.toString())

            })
    }







    private fun DocumentSnapshot.toTimeSlotObject(): TimeSlot? {
        return try {
            val title = get("title") as String
            val description = get("description") as String
            val date = get("date") as String
            val time = get("time") as String
            val duration = get("duration") as String
            val location = get("location") as String
            val index = get("index") as Long

            TimeSlot(title, description, date, time, duration, location, index.toInt())

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("prova4", e.toString())
            null
        }


    }*/

}








