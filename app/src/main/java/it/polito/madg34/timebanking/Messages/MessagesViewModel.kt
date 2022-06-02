package it.polito.madg34.timebanking.Messages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Profile.ProfileUser
import java.lang.Exception

class MessagesViewModel : ViewModel() {

    val currentUserMessages : MutableLiveData<List<Message>> =
        MutableLiveData<List<Message>>().also { loadMessages() }

    val allMessages : MutableLiveData<List<Message>> =
        MutableLiveData<List<Message>>().also { getAllUnreadMessages() }

    var otherUserEmail = ""
    var currentRelatedAdv = ""

    var receivedReqNumber = 0
    var sentNumber = 0
    var unreadMessagesOnCard = 0

    private var listener1: ListenerRegistration? = null


    fun loadMessages() {
        if(!currentRelatedAdv.isNullOrEmpty()){
            listener1 = FirestoreRepository().getChatMessages(currentRelatedAdv).addSnapshotListener(
                EventListener { value, e ->
                    if (e != null) {
                        currentUserMessages.value = emptyList()
                        return@EventListener
                    }
                    if (value!!.documents.size > 0) {
                        currentUserMessages.value = value.documents.filter {
                            (it.getString("SENT_BY") == FirestoreRepository.currentUser.email &&
                                    it.getString("RECEIVED_BY") == otherUserEmail)
                                    ||
                                    (it.getString("SENT_BY") == otherUserEmail &&
                                            it.getString("RECEIVED_BY") == FirestoreRepository.currentUser.email)
                        }.mapNotNull { d -> d.toMessageObject() }
                        currentUserMessages.value = currentUserMessages.value?.sortedBy {
                            it.timeStamp
                        }
                        Log.d("EMAIL", currentUserMessages.value.toString())
                    }
                    else currentUserMessages.value = emptyList()
                })
        }
    }

    private fun getAllUnreadMessages() {
        FirestoreRepository().getAllMessages().addSnapshotListener(EventListener{ value, e ->
            if (e != null) {
                allMessages.value = emptyList()
                return@EventListener
            }
            if(value!!.documents.size > 0){
                allMessages.value = value.documents.mapNotNull { d -> d.toMessageObject() }
            }
        })
    }

    fun getCurrentUserMessages(): LiveData<List<Message>> {
        return currentUserMessages
    }

    fun getAllMessages() : LiveData<List<Message>>{
        return allMessages
    }

    fun sendNewMessage(messageContent: String): Task<Void> {
        val newMessage = Message("",
            messageContent, 0, FirestoreRepository.currentUser.email!!, otherUserEmail,
            System.currentTimeMillis(), currentRelatedAdv
        )

        return FirestoreRepository().setMessage(newMessage)
    }

    fun sendAutoRejectMessage(messageContent: String, to : String): Task<Void> {
        val newMessage = Message("",
            messageContent, 0, FirestoreRepository.currentUser.email!!, to,
            System.currentTimeMillis(), currentRelatedAdv
        )

        return FirestoreRepository().setMessage(newMessage)
    }

    fun updateMessageRead(message : Message): Task<Void> {
        return FirestoreRepository().updateMessageReadDB(message.id)
    }

    fun modifyProfileInChat(email: String, duration: String?) {
        FirestoreRepository().getUserFromEmail(email).get().addOnSuccessListener {
            var profileInChat: ProfileUser
            if (it != null) {
                profileInChat = it.toObject(ProfileUser::class.java)!!
                var newTotalTime = convertTotalTime(profileInChat.total_time, duration)
                if(newTotalTime != "false"){
                    profileInChat.total_time = newTotalTime
                    FirestoreRepository().setOtherUser(email, profileInChat)
                }
            }
        }
    }

    private fun convertTotalTime(otherTime: String?, duration: String?): String {
        Log.d("hm-1", duration!!)
        var item1 = otherTime?.split(":")?.toTypedArray()
        var sxItem1 = item1?.get(0)?.removeSuffix("h")
        var dxItem1 = item1?.get(1)?.removeSuffix("m")

        var item2 = duration?.split(":")?.toTypedArray()
        var sxItem2 = item2?.get(0)?.removeSuffix("h")
        var dxItem2 = item2?.get(1)?.removeSuffix("m")

        if(sxItem1?.toInt()!! < sxItem2.toInt()){
            return "false"
        }else if (sxItem1?.toInt()!! == sxItem2.toInt()){
            if (dxItem1?.toInt()!! < dxItem2.toInt())
                return "false"
        }

        var m1 = (sxItem1?.toInt()!! * 60) + dxItem1!!.toInt()
        var m2 = (sxItem2?.toInt()!! * 60) + dxItem2.toInt()

        var minus = m1 - m2
        Log.d("hm-2", minus.toString())

        var i = 0
        var h = 0
        var m = 0
        while (i < minus) {
            m++
            if (m == 60) {
                h++
                m = 0
            }
            i++
        }
        Log.d("hm-3", h.toString())
        Log.d("hm-4", m.toString())

        return h.toString() + "h" + ":" + m.toString() + "m"
    }

    private fun DocumentSnapshot.toMessageObject(): Message? {
        return try {
            val id = get("ID") as String
            val messageContent = get("MESSAGE_CONTENT") as String
            val read = get("READ") as Long
            val sentBy = get("SENT_BY") as String
            val received_by = get("RECEIVED_BY") as String
            val timestamp = get("TIMESTAMP") as Long
            val relatedAdv = get("RELATED_ADV") as String

            Message(id, messageContent, read.toInt(), sentBy, received_by, timestamp, relatedAdv)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}