package it.polito.madg34.timebanking.Chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Messages.MessagesViewModel
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot

class ChatAdapter(val chatList: List<Chat>, val timeSlots : List<TimeSlot>) : RecyclerView.Adapter<ChatViewHolder>() {
    lateinit var v: View

    lateinit var vmProfile: ProfileViewModel
    lateinit var vmMessages: MessagesViewModel
    lateinit var vmChat: ChatViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        v = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list_view_holder, parent, false)
        return ChatViewHolder(v)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        vmProfile = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmMessages = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmChat = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        val a = holder.itemView.context as AppCompatActivity
        val b = a.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = b.navController

        val chatEntry = chatList[position].info
        val split = chatEntry.split(",")
        val chatEntryAdv = split[0]
        var chatEntryEmail = split[1]
        val timeSlot = timeSlots.find { it.id == chatEntryAdv }

        if(vmChat.sentOrReceived.value == true){ // received
            chatEntryEmail = FirestoreRepository.currentUser.email!!
        }
        vmProfile.loadChatImage(chatEntryEmail).addOnSuccessListener {
            if(it != null){
                if (timeSlot != null) {
                    holder.bind(timeSlot, vmProfile.chatImage.value)
                }
            }
        }

        holder.itemView.setOnClickListener{
            Log.d("ADV", chatEntryAdv)
            vmMessages.currentRelatedAdv = chatEntryAdv
            vmMessages.otherUserEmail = chatEntryEmail
            vmMessages.loadMessages()
            navController.navigate(R.id.action_chatFragment_to_messageFragment)
        }
    }

    override fun getItemCount(): Int = chatList.size
}