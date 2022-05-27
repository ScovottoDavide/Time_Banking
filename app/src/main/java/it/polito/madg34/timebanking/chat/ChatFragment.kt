package it.polito.madg34.timebanking.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.ProfileViewModel
import it.polito.madg34.timebanking.R

class ChatFragment : Fragment() {
    val vmChat: ChatViewModel by activityViewModels()
    val vmProfile: ProfileViewModel by activityViewModels()

    private var chatList: MutableList<String> = mutableListOf()
    lateinit var chatRV: RecyclerView

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

        /*vmProfile.chatImage.observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                vmProfile.currentChatImage = it
            }
        }*/
        vmChat.getCurrentChatList().observe(viewLifecycleOwner){
            if(!it.isNullOrEmpty()){
                chatList = it as MutableList<String>
                chatRV = view.findViewById(R.id.ChatList)
                chatRV.layoutManager = LinearLayoutManager(this.context)
                chatRV.adapter = ChatAdapter(chatList)
            }
        }
    }
}