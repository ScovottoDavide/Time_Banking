package it.polito.madg34.timebanking.Chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.HomeSkills.Skills
import it.polito.madg34.timebanking.TimeSlots.TimeSlot

class ChatViewModel : ViewModel() {
    val currentChatList: MutableLiveData<MutableList<Chat>> = MutableLiveData<MutableList<Chat>>().also { loadChatSent() }
    val currentChatReceivedList: MutableLiveData<MutableList<Chat>> = MutableLiveData<MutableList<Chat>>().also { loadChatReceived() }

    val sentOrReceived: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false) // false = sent, true = received

    var stringChat = ""

    private var listener1: ListenerRegistration? = null
    private var listener2: ListenerRegistration? = null

    var selection = MutableLiveData(0)
    var filtered: MutableLiveData<Boolean> = MutableLiveData(false)
    var filteredTimeSlots : MutableList<TimeSlot> = mutableListOf()
    var filteredChat : MutableList<Chat> = mutableListOf()

    private fun loadChatSent() {
        listener1 = FirestoreRepository().getAllChatEmail()
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    currentChatList.value = mutableListOf()
                    return@EventListener
                }
                if (value?.get("INFO") != null) {
                    val localStirng = value.get("INFO") as String
                    stringChat = localStirng
                    val listString = localStirng.split("|")
                    val tmp: MutableList<Chat> = mutableListOf()
                    listString.forEach {
                        tmp.add(Chat(it))
                    }
                    currentChatList.value = tmp
                }
            })
    }

    private fun loadChatReceived() {
        listener2 = FirestoreRepository().getAllChatReceived().addSnapshotListener(EventListener{ value, e ->
            if (e != null) {
                currentChatList.value = mutableListOf()
                return@EventListener
            }
            if(value!!.documents.size > 0){
                val tmpList : MutableList<Chat> = mutableListOf()
                value.documents.forEach { doc ->
                    val allInfo = doc.getString("INFO")
                    val split = allInfo!!.split("|")
                    split.forEach {
                        if(it.contains(FirestoreRepository.currentUser.email!!)){
                            val subSplit = it.split(",")
                            tmpList.add(Chat(subSplit[0]+","+doc.id))
                        }
                    }
                }
                currentChatReceivedList.value = tmpList
            }
        })
    }

    fun getCurrentChatList(): LiveData<MutableList<Chat>> {
        return currentChatList
    }

    fun getCurrentChatReceivedList(): LiveData<MutableList<Chat>> {
        return currentChatReceivedList
    }

    fun newChatAdd(newChat: Chat): Task<Void>? {
        if (stringChat.isEmpty()) {
            stringChat = newChat.info
        } else {
            stringChat = stringChat + "|" + newChat.info
        }
        currentChatList.value?.add(newChat)
        return FirestoreRepository().addChat(stringChat)
    }

}