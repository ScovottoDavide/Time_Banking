package it.polito.madg34.timebanking.Chat

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.HomeSkills.SkillsViewModel
import it.polito.madg34.timebanking.Messages.MessagesViewModel
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel

class ChatAdapter(val chatList: List<Chat>, val timeSlots: List<TimeSlot>) :
    RecyclerView.Adapter<ChatViewHolder>() {
    lateinit var v: View

    lateinit var vmProfile: ProfileViewModel
    lateinit var vmMessages: MessagesViewModel
    lateinit var vmChat: ChatViewModel
    lateinit var vmTimeSlot: TimeSlotViewModel
    lateinit var vmSkills: SkillsViewModel

    lateinit var chatButton: ImageButton
    lateinit var topCardTitle: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        v = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list_view_holder, parent, false)
        return ChatViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        vmProfile = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmMessages = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmChat = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmTimeSlot = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmSkills = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        chatButton = holder.itemView.findViewById(R.id.chatButton)
        topCardTitle = holder.itemView.findViewById(R.id.requester)

        val a = holder.itemView.context as AppCompatActivity
        val b =
            a.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = b.navController

        val chatEntry = chatList[position].info
        val split = chatEntry.split(",")
        val chatEntryAdv = split[0]
        val chatEntryEmail = split[1]
        var profileImageReceived = ""
        val timeSlot = timeSlots.find { it.id == chatEntryAdv }

        if (vmChat.sentOrReceived.value == true) { // received
            profileImageReceived = FirestoreRepository.currentUser.email!!
            vmProfile.loadChatImage(profileImageReceived).addOnSuccessListener {
                if (it != null) {
                    if (timeSlot != null) {
                        holder.bind(timeSlot, vmProfile.chatImage.value, chatEntryEmail)
                    }
                }
            }
            topCardTitle.setText("${chatEntryEmail} contacted you")
        } else {
            profileImageReceived = chatEntryEmail
            vmProfile.loadChatImage(profileImageReceived).addOnSuccessListener {
                if (it != null) {
                    if (timeSlot != null) {
                        holder.bind(timeSlot, vmProfile.chatImage.value, FirestoreRepository.currentUser.email!!)
                    }
                }
            }
            topCardTitle.setText("You contacted ${profileImageReceived}")
        }

        chatButton.setOnClickListener {
            vmMessages.currentRelatedAdv = chatEntryAdv
            vmMessages.otherUserEmail = chatEntryEmail
            vmTimeSlot.currentShownAdv = timeSlot
            vmMessages.loadMessages()
            navController.navigate(R.id.action_chatFragment_to_messageFragment)
        }

        holder.itemView.setOnClickListener {
            vmTimeSlot.currentShownAdv = timeSlot
            vmSkills.fromHome.value = true
            navController.navigate(R.id.action_chatFragment_to_timeSlotDetailsFragment)
        }
    }

    override fun getItemCount(): Int = chatList.size
}