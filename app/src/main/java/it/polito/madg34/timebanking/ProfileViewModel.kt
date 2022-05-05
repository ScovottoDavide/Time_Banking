package it.polito.madg34.timebanking

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ProfileViewModel(application: Application) : AndroidViewModel(application){

    var sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    private var  gson : Gson = Gson()

    //val p = clear()

    var _profile = MutableLiveData<ProfileUser>().also{
        if(sharedPref.contains("ProfileUser")){
            val type : Type = object : TypeToken<ProfileUser>() {}.type
            it.value = gson.fromJson(sharedPref.getString("listServices", null), type)
        }
    }

    var profile : LiveData<ProfileUser> = _profile
    fun saveServices(v : ProfileUser){
        _profile.value = v
        val serialized = gson.toJson(v)
        println("SAVING")
        sharedPref.edit().putString("ProfileUser", serialized).apply()
    }

    fun clear(){
        sharedPref.edit().clear().apply()
    }
}