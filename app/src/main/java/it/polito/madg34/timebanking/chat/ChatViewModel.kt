package it.polito.madg34.timebanking.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.TimeSlot

class ChatViewModel : ViewModel() {
    val currentChatList: MutableLiveData<List<String>> = MutableLiveData<List<String>>().also { loadChatReceived() }

    private var listener1: ListenerRegistration? = null

    private fun loadChatReceived() {
        listener1 = FirestoreRepository().getAllChatEmail()
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    currentChatList.value = emptyList()
                    return@EventListener
                }
                if(value?.data != null){
                    val a = value?.data as Map<String,Map<String,String>>
                    currentChatList.value = a.keys.toList()
                }else{
                    currentChatList.value = emptyList()
                }

            })
    }

    fun getCurrentChatList():LiveData<List<String>>{
        return currentChatList
    }


}