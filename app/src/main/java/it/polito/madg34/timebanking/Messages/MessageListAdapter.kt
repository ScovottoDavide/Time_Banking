package it.polito.madg34.timebanking.Messages


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class MessageListAdapter(val messageList: List<Message>) :
    RecyclerView.Adapter<ReceivedMessageHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceivedMessageHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holderReceived: ReceivedMessageHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = messageList.size

}