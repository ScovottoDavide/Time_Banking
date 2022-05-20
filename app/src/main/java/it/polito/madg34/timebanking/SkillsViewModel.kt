package it.polito.madg34.timebanking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

class SkillsViewModel : ViewModel(){

    val allSkills : MutableLiveData<MutableMap<String, Skills>> by lazy { MutableLiveData<MutableMap<String, Skills>>().also { loadAllSkills() } }
    val localSkills : MutableMap<String, Skills> = mutableMapOf()

    private var listener1: ListenerRegistration? = null

    private fun loadAllSkills() {
        listener1 = FirestoreRepository().getAllSkills().addSnapshotListener(EventListener{value, e ->
            if (e != null) {
                allSkills.value = null
                return@EventListener
            }
            value?.documents?.forEach {
                val tmp = it.getString("RELATED_ADVS")
                if (tmp != null) {
                    localSkills.set(it.id, Skills(tmp))
                }
            }
            allSkills.value = localSkills
            allSkills.value?.keys.toString()?.let { it1 -> Log.d("VM", it1) }
        })
    }

    fun getAllSkillsVM(): LiveData<MutableMap<String, Skills>> {
        return allSkills
    }

}