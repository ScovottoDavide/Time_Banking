package it.polito.madg34.timebanking.Messages

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.EditText
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import it.polito.madg34.timebanking.Chat.ChatViewModel
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel

class MessageFragment : Fragment() {
    val vmMessage: MessagesViewModel by activityViewModels()
    val vmChat: ChatViewModel by activityViewModels()
    val vmTimeSlot: TimeSlotViewModel by activityViewModels()

    var messagesToDisplay : List<Message> = emptyList()

    lateinit var messagesRV : RecyclerView

    lateinit var sendButton : Button
    lateinit var messageContent : EditText
    lateinit var accept : MaterialButton
    lateinit var deny : MaterialButton

    private var h: Int = 0
    private var w: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.messages_list_fragment, container, false)
        halfWidth(view)
        //setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendButton = view.findViewById(R.id.button_gchat_send)
        messageContent = view.findViewById(R.id.edit_gchat_message)
        sendButton.setOnClickListener {
            if(messageContent.text.isNotEmpty())
                vmMessage.sendNewMessage(messageContent.text.toString())
            messageContent.setText("")
        }

        if(vmChat.sentOrReceived.value == true){ // received
            if(vmTimeSlot.currentShownAdv?.refused?.isEmpty() == true && vmTimeSlot.currentShownAdv?.accepted?.isEmpty() == true){
                accept.visibility = View.VISIBLE
                deny.visibility = View.VISIBLE
            }else{
                accept.visibility = View.GONE
                deny.visibility = View.GONE
            }
        } else {
            accept.visibility = View.GONE
            deny.visibility = View.GONE
        }

        vmMessage.getCurrentUserMessages().observe(viewLifecycleOwner){
            if(it != null){
                messagesToDisplay = it
                val messagesRead = messagesToDisplay.filter { it.sentBy != FirestoreRepository.currentUser.email!! }
                messagesRead.forEach { m -> vmMessage.updateMessageRead(m) }
                messagesRV = view.findViewById(R.id.recycler_gchat)
                messagesRV.layoutManager = LinearLayoutManager(this.context)
                messagesRV.adapter = MessageListAdapter(messagesToDisplay)
                if(messagesToDisplay.isNotEmpty())
                    messagesRV.smoothScrollToPosition(messagesToDisplay.size - 1)
            }
        }

        /** 1. Update available field of timeslot
         *  2. Update accepted field of timeslot
         *  3. Remove timeslot from online (done through point 1)
         *
         * */
        accept.setOnClickListener{
            vmTimeSlot.currentShownAdv?.available = 0
            vmTimeSlot.currentShownAdv?.accepted = vmMessage.otherUserEmail
            vmTimeSlot.currentShownAdv?.let { it1 -> vmTimeSlot.updateAdv(it1) }
        }

        deny.setOnClickListener {

        }
    }

    private fun halfWidth(view: View) {
        val row = view.findViewById<TableRow>(R.id.RowAcceptDeny)
        accept = view.findViewById(R.id.RowAccept)
        deny = view.findViewById(R.id.RowDeny)

        row.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    h = row.height
                    w = row.width
                    accept.post { accept.layoutParams = TableRow.LayoutParams(w / 2, h) }
                    deny.post { deny.layoutParams = TableRow.LayoutParams(w / 2, h) }
                } else {
                    h = row.height
                    w = row.width
                    accept.post { accept.layoutParams = TableRow.LayoutParams(w / 2, h) }
                    deny.post { deny.layoutParams = TableRow.LayoutParams(w / 2, h) }
                }
                row.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}