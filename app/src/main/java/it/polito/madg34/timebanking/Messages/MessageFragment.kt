package it.polito.madg34.timebanking.Messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.R

class MessageFragment : Fragment() {
    val vmMessage: MessagesViewModel by activityViewModels()

    var messagesToDisplay : List<Message> = emptyList()

    lateinit var messagesRV : RecyclerView

    lateinit var sendButton : Button
    lateinit var messageContent : EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.messages_list_fragment, container, false)
        //setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendButton = view.findViewById(R.id.button_gchat_send)
        messageContent = view.findViewById(R.id.edit_gchat_message)
        sendButton.setOnClickListener {
            vmMessage.sendNewMessage(messageContent.text.toString())
            messageContent.setText("")
        }

        vmMessage.getCurrentUserMessages().observe(viewLifecycleOwner){
            if(it != null){
                messagesToDisplay = it
                Log.d("MESSAGES", messagesToDisplay.toString())
                messagesRV = view.findViewById(R.id.recycler_gchat)
                messagesRV.layoutManager = LinearLayoutManager(this.context)
                messagesRV.adapter = MessageListAdapter(messagesToDisplay)
            }
        }
    }
}