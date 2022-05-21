package it.polito.madg34.timebanking

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import java.lang.Exception

class SkillsViewModel : ViewModel(){

    val allSkills : MutableLiveData<MutableMap<String, Skills>> by lazy { MutableLiveData<MutableMap<String, Skills>>().also { loadAllSkills() } }
    var localSkills : MutableMap<String, Skills> = mutableMapOf()

    /** Advs to be displayed after click on skill of home page */
    var stringAdvs : String = ""
    val currentSkillAdvs : MutableLiveData<List<TimeSlot>?> by lazy { MutableLiveData<List<TimeSlot>?>().also { loadSkillAdvs() }}

    var fromHome : MutableLiveData<Boolean> = MutableLiveData(false)

    private var listener1: ListenerRegistration? = null
    private var listener2: ListenerRegistration? = null

    fun loadAllSkills() {
        listener1 = FirestoreRepository().getAllSkills().addSnapshotListener{value, e ->
            if (e != null) {
                allSkills.value = null
                return@addSnapshotListener
            }
            // reinitialize the map, otherwise also cancelled skills remain!!
            localSkills = mutableMapOf()
            value?.documents?.forEach {
                val tmp = it.getString("RELATED_ADVS")
                if (tmp != null) {
                    localSkills.set(it.id, Skills(tmp))
                }
                allSkills.value = localSkills
            }
        }
    }

    fun loadSkillAdvs() {
        val listAdvsToRetrieve = stringAdvs.split(",")
        val tmpList : MutableList<TimeSlot> = mutableListOf()
        listAdvsToRetrieve.forEach { adv ->
            listener2 = FirestoreRepository().getAdvFromDocId(adv)
                ?.addSnapshotListener(EventListener{value, e ->
                    if(e != null){
                        currentSkillAdvs.value = null
                        return@EventListener
                    }
                    value!!.toTimeSlotObject()?.let { tmpList.add(it) }
                    currentSkillAdvs.value = tmpList
            })
        }
    }

    fun getAllSkillsVM(): LiveData<MutableMap<String, Skills>> {
        return allSkills
    }

    fun getAdvsToDisplayFromSkill() : LiveData<List<TimeSlot>?> {
        return currentSkillAdvs
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