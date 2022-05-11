package it.polito.madg34.timebanking

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ProfileViewModel(application: Application) : AndroidViewModel(application){

    var sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    private var  gson : Gson = Gson()

    private val db : FirebaseFirestore
        init {
            db = FirebaseFirestore.getInstance()
        }

    //val p = clear()

    var _profile = MutableLiveData<ProfileUser>().also{

         /*db
             .collection("users")
             .document("u1")
             .get()
             .addOnSuccessListener { res ->
                val value = res.toObject(ProfileUser::class.java)



             }
             .addOnFailureListener{
                 Toast.makeText(getApplication(), "No user", Toast.LENGTH_SHORT).show()

             }*/
        if(sharedPref.contains("ProfileUser")){
            val type : Type = object : TypeToken<ProfileUser>() {}.type
            it.value = gson.fromJson(sharedPref.getString("ProfileUser", null), type)

        }
    }

    var profile : LiveData<ProfileUser> =  _profile

    fun modifyUser(v : ProfileUser){
        db.collection("users").document("u1").set(mapOf("uri" to v.img,"FULLNAME" to v.fullName,"NICKNAME" to v.nickname,"EMAIL" to v.email, "LOCATION" to v.location, "ABOUT_ME" to v.aboutUser,
        "Skills" to v.skills
        ))
            .addOnSuccessListener {
                Toast.makeText(getApplication(), "OK", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener{
                Toast.makeText(getApplication(), "Fail", Toast.LENGTH_SHORT).show()

            }

    }

    fun saveProfile(v : ProfileUser){
        _profile.value = v
        val serialized = gson.toJson(v)
        println("SAVING")
        sharedPref.edit().putString("ProfileUser", serialized).apply()
    }

    fun clear(){
        sharedPref.edit().clear().apply()
    }
}