package it.polito.madg34.timebanking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.TypedArray
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.sql.Time
import java.time.format.DateTimeFormatter
import kotlin.reflect.typeOf


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
}