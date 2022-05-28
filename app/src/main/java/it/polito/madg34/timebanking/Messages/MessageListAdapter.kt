package it.polito.madg34.timebanking.Messages


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R


class MessageListAdapter(val messageList: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var v: View

    lateinit var vmProfile: ProfileViewModel

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if(message.sentBy == FirestoreRepository.currentUser.email)
            VIEW_TYPE_MESSAGE_SENT
        else
            VIEW_TYPE_MESSAGE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == VIEW_TYPE_MESSAGE_SENT){
            v = LayoutInflater.from(parent.context).inflate(R.layout.message_send_layout, parent, false)
            return SentMessageHolder(v)
        } else if(viewType == VIEW_TYPE_MESSAGE_RECEIVED){
            v = LayoutInflater.from(parent.context).inflate(R.layout.message_receive_layout, parent, false)
            return ReceivedMessageHolder(v)
        }
        return SentMessageHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        vmProfile = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        val message = messageList[position]
        vmProfile.clickedEmail.value = message.sentBy
        vmProfile.loadViewProfile().addOnSuccessListener {
            when(holder.itemViewType){
                VIEW_TYPE_MESSAGE_SENT -> {
                    (holder as SentMessageHolder).bind(message)
                }
                VIEW_TYPE_MESSAGE_RECEIVED -> {
                    (holder as ReceivedMessageHolder).bind(message, vmProfile.profileToShow?.img, vmProfile.profileToShow?.nickname)
                }
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

}