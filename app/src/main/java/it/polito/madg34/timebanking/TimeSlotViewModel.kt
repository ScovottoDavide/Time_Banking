package it.polito.madg34.timebanking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import com.google.gson.Gson


class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {

    var sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    private var  gson : Gson = Gson()

    //val p = clear()

    var _title_vm = MutableLiveData<String>().also{
        if(sharedPref.contains("title")){
            it.value = gson.fromJson(sharedPref.getString("title", "Ciao"), String::class.java)
        }
    }
    var _description_vm = MutableLiveData<String>().also{
        if(sharedPref.contains("description")){
            it.value = gson.fromJson(sharedPref.getString("description", "Ciao"), String::class.java)
        }
    }
    var _date_vm = MutableLiveData<String>().also{
        if(sharedPref.contains("date")){
            it.value = gson.fromJson(sharedPref.getString("date", "Ciao"), String::class.java)
        }
    }
    var _time_vm = MutableLiveData<String>().also{
        if(sharedPref.contains("time")){
            it.value = gson.fromJson(sharedPref.getString("time", "Ciao"), String::class.java)
        }
    }
    var _duration_vm = MutableLiveData<String>().also{
        if(sharedPref.contains("duration")){
            it.value = gson.fromJson(sharedPref.getString("duration", "Ciao"), String::class.java)
        }
    }
    var _location_vm = MutableLiveData<String>().also{
        if(sharedPref.contains("location")){
            it.value = gson.fromJson(sharedPref.getString("location", "Ciao"), String::class.java)
        }
    }

    val title_vm : LiveData<String> = _title_vm
    val description_vm : LiveData<String> = _description_vm
    val date_vm : LiveData<String> = _date_vm
    val time_vm : LiveData<String> = _time_vm
    val duration_vm : LiveData<String> = _duration_vm
    val location_vm : LiveData<String> = _location_vm

    fun m_title(s: String){
        _title_vm.value = s
        val serialized = gson.toJson(s)
        sharedPref.edit().putString("title",serialized).apply()
    }

    fun m_description(s: String){
        _description_vm.also { it.value = s }
        val serialized = gson.toJson(s)
        sharedPref.edit().putString("description",serialized).apply()
    }

    fun m_date(s: String?){
        _date_vm.also { it.value = s }
        val serialized = gson.toJson(s)
        sharedPref.edit().putString("date",serialized).apply()
    }

    fun m_time(s: String?){
        _time_vm.also { it.value = s }
        val serialized = gson.toJson(s)
        sharedPref.edit().putString("time",serialized).apply()
    }

    fun m_duration(s: String){
        _duration_vm.also { it.value = s }
        val serialized = gson.toJson(s)
        sharedPref.edit().putString("duration",serialized).apply()
    }

    fun m_location(s: String){
        _location_vm.also { it.value = s }
        val serialized = gson.toJson(s)
        sharedPref.edit().putString("location",serialized).apply()
    }

    fun clear(){
        sharedPref.edit().clear().apply()
    }
}