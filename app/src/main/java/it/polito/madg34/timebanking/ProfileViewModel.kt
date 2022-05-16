package it.polito.madg34.timebanking

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ProfileViewModel : ViewModel() {

    /*var sharedPref: SharedPreferences = getApplication<Application>().getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
    private var  gson : Gson = Gson()*/



    //val p = clear()

    /*var _profile : MutableLiveData<ProfileUser> = MutableLiveData<ProfileUser>() .also{

        db
             .collection("users")
             .document("u1")
             .get()
             .addOnSuccessListener { res ->
                 val value = res.toObject(ProfileUser::class.java)
                 value

             }
             .addOnFailureListener{
                 Toast.makeText(getApplication(), "No user", Toast.LENGTH_SHORT).show()

             }

        /*if(sharedPref.contains("ProfileUser")){
            val type : Type = object : TypeToken<ProfileUser>() {}.type
            it.value = gson.fromJson(sharedPref.getString("ProfileUser", null), type)

        }*/
    }*/

    var localProfile : ProfileUser? = ProfileUser()

    val profile: MutableLiveData<ProfileUser> by lazy { MutableLiveData(ProfileUser()).also { loadProfile() } }
    var needRegistration = false
    //var profile : LiveData<ProfileUser> =  _profile
    //private val db :FirebaseFirestore
    private var listener1 : ListenerRegistration? = null
    /*init {
        db = FirebaseFirestore.getInstance()
        l = FirebaseFirestore.getInstance().collection("users").document("u1").addSnapshotListener{ r, e ->
            if (r != null) {
                _profile.value = if(e!=null) {
                    emptyProfile()
                    return@addSnapshotListener
                }
                else {
                       r.toObject<ProfileUser>()

                }
            }

        }
    }*/

    private fun loadProfile() {
        listener1 = FirestoreRepository().getUser().addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                profile.value = null
                return@EventListener
            }
            profile.value = value?.toObject(ProfileUser::class.java)
        })
    }

    fun getDBUser() : LiveData<ProfileUser>{
        return profile
    }
    /*var d = {
        Log.d("PORCO23", _profile.value.toString())
        Log.d("PORCO32", profile.value.toString())
    }
    var y = d()*/


    /*fun modifyUser(v : ProfileUser){
        FirebaseFirestore.getInstance().collection("users").document("u1").set(v)
            .addOnSuccessListener {
                Toast.makeText(getApplication(), "OK", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener{
                Toast.makeText(getApplication(), "Fail", Toast.LENGTH_SHORT).show()

            }

    }*/
    fun modifyUserProfile(value : ProfileUser) : Task<Void> {
        return FirestoreRepository().setUser(value)
    }

    /*fun saveProfile(v : ProfileUser){
        _profile.value = v
        val serialized = gson.toJson(v)
        println("SAVING")
        sharedPref.edit().putString("ProfileUser", serialized).apply()
    }

    fun clear(){
        sharedPref.edit().clear().apply()
    }*/
}