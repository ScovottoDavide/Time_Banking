package it.polito.madg34.timebanking.HomeSkills

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import java.lang.Exception

class SkillsViewModel : ViewModel() {

    val allSkills: MutableLiveData<MutableMap<String, Skills>> by lazy { MutableLiveData<MutableMap<String, Skills>>().also { loadAllSkills() } }
    var localSkills: MutableMap<String, Skills> = mutableMapOf()

    /** Advs to be displayed after click on skill of home page */
    var stringAdvs: String = ""
    val currentSkillAdvs: MutableLiveData<List<TimeSlot>?> by lazy { MutableLiveData<List<TimeSlot>?>().also { loadSkillAdvs() } }

    var fromHome: MutableLiveData<Boolean> = MutableLiveData(false)

    var filtered: MutableLiveData<Boolean> = MutableLiveData(false)
    var filteredSkills: MutableList<String> = mutableListOf()
    var filteredAdvs: MutableList<Skills> = mutableListOf()
    var selection = MutableLiveData(0)

    var viewProfilePopupOpen = false

    private var listener1: ListenerRegistration? = null
    private var listener2: ListenerRegistration? = null

    fun loadAllSkills() {
        listener1 = FirestoreRepository().getAllSkills().addSnapshotListener { value, e ->
            if (e != null) {
                allSkills.value = null
                return@addSnapshotListener
            }
            // reinitialize the map, otherwise also cancelled skills remain!!
            localSkills = mutableMapOf()
            value?.documents?.forEach {
                val tmp = it.getString("RELATED_ADVS")?.split(",")
                tmp?.forEach { t ->
                    FirestoreRepository().getAdvFromDocId(t)?.get()?.addOnSuccessListener { d ->
                        if (d.get("AVAILABLE")?.toString()?.toInt() != 0){
                            if(localSkills.get(it.id) == null)
                                localSkills.put(it.id, Skills(t))
                            else{
                                val inMap = localSkills.get(it.id)
                                inMap?.relatedAdvs = inMap?.relatedAdvs + "," + t
                                localSkills.put(it.id, inMap!!)
                            }
                        }
                        allSkills.value = localSkills
                    }
                }
            }
            if (value?.documents?.isEmpty() == true)
                allSkills.value = mutableMapOf()
        }
    }

fun loadSkillAdvs() {
    val listAdvsToRetrieve = stringAdvs.split(",")
    var tmpList: MutableList<TimeSlot> = mutableListOf()
    Log.d("PROVA", listAdvsToRetrieve.toString())
    listAdvsToRetrieve.forEach { adv ->
        listener2 = FirestoreRepository().getAdvFromDocId(adv)
            ?.addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    currentSkillAdvs.value = null
                    return@EventListener
                }
                value!!.toTimeSlotObject()?.let { t ->
                    if(tmpList.map { it.id }.contains(t.id)){
                       val index =  tmpList.map { it.id }.indexOf(t.id)
                        tmpList[index] = t
                    }else{
                        tmpList.add(t)
                    }
                }
                currentSkillAdvs.value = tmpList
            })
    }
}

fun getAllSkillsVM(): LiveData<MutableMap<String, Skills>> {
    return allSkills
}

fun getAdvsToDisplayFromSkill(): LiveData<List<TimeSlot>?> {
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
        val available = get("AVAILABLE") as Long
        val index = get("INDEX") as Long
        val reviews = get("REVIEWS") as String

        TimeSlot(
            id, title, description, date, time, duration, location, email, related_skill,
            index.toInt(), available.toInt(), reviews
        )

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
}