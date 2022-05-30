package it.polito.madg34.timebanking.Chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel

class ChatFragment : Fragment() {
    val vmChat: ChatViewModel by activityViewModels()
    val vmTimeSlot: TimeSlotViewModel by activityViewModels()

    private var chatList: List<Chat> = listOf()
    private var chatReceivedList: List<Chat> = listOf()
    private var timeSlots : MutableList<TimeSlot> = mutableListOf()
    private var timeSlotsIds : MutableList<String> = mutableListOf()

    lateinit var chatRV: RecyclerView
    lateinit var titlePage : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_list_fragment, container, false)
        //setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titlePage = view.findViewById(R.id.chatTitle)

        vmChat.sentOrReceived.observe(viewLifecycleOwner){ sentOrreceived ->
            if(sentOrreceived){ // received
                titlePage.setText("Incoming Requests")
                vmChat.getCurrentChatReceivedList().observe(viewLifecycleOwner){
                    if(!it.isNullOrEmpty()){
                        chatReceivedList = it
                        chatReceivedList.forEach {
                            it.info.split("|").forEach { s ->
                                timeSlotsIds.add(s.split(",")[0])
                            }
                        }
                        vmTimeSlot.loadAdvByIds(timeSlotsIds)
                        vmTimeSlot.getChatTimeSlots().observe(viewLifecycleOwner){ it1 ->
                            if(it1 != null){
                                timeSlots = it1
                                chatRV = view.findViewById(R.id.ChatList)
                                chatRV.layoutManager = LinearLayoutManager(this.context)
                                chatRV.adapter = ChatAdapter(chatReceivedList, timeSlots)
                            }
                        }
                    }
                }
            }else {
                titlePage.setText("Outgoing Requests")
                vmChat.getCurrentChatList().observe(viewLifecycleOwner){ it1 ->
                    if(!it1.isNullOrEmpty()){
                        chatList = it1
                        chatList.forEach { c ->
                            c.info.split("|").forEach { s ->
                                timeSlotsIds.add(s.split(",")[0])
                            }
                        }
                        vmTimeSlot.loadAdvByIds(timeSlotsIds)
                        vmTimeSlot.getChatTimeSlots().observe(viewLifecycleOwner){ it2 ->
                            if(it2 != null){
                                timeSlots = it2
                                chatRV = view.findViewById(R.id.ChatList)
                                chatRV.layoutManager = LinearLayoutManager(this.context)
                                chatRV.adapter = ChatAdapter(chatList, timeSlots)
                            }
                        }
                    }
                }
            }
        }
    }
}