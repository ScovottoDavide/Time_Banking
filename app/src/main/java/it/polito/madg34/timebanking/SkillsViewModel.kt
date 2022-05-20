package it.polito.madg34.timebanking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

class SkillsViewModel : ViewModel(){

    val allSkills : MutableLiveData<MutableMap<String, Skills>> by lazy { MutableLiveData<MutableMap<String, Skills>>().also { loadAllSkills() } }

    private var listener1: ListenerRegistration? = null

    private fun loadAllSkills() {
        listener1 = FirestoreRepository().getAllSkills().addSnapshotListener(EventListener{value, e ->
            if (e != null) {
                allSkills.value = null
                return@EventListener
            }
            value?.documents?.forEach {
                val tmp = it.getString("RELATED_ADVS")?.let { it1 -> Skills(it1) }
                if (tmp != null) {
                    allSkills.value?.set(it.id, tmp)
                }
            }
        })
    }

    fun getAllSkillsVM(): LiveData<MutableMap<String, Skills>> {
        return allSkills
    }

}