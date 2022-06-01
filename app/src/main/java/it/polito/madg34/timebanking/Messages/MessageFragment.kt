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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import it.polito.madg34.timebanking.Chat.ChatViewModel
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Profile.ProfileUser
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel

class MessageFragment : Fragment() {
    val vmMessage: MessagesViewModel by activityViewModels()
    val vmChat: ChatViewModel by activityViewModels()
    val vmTimeSlot: TimeSlotViewModel by activityViewModels()
    val vmProfile: ProfileViewModel by activityViewModels()

    private var messagesToDisplay : List<Message> = emptyList()

    lateinit var messagesRV : RecyclerView


    lateinit var sendButton : Button
    lateinit var messageContent : EditText
    lateinit var accept : MaterialButton
    lateinit var deny : MaterialButton
    lateinit var alertDialog : AlertDialog
    lateinit var alertDecline : AlertDialog

    private var h: Int = 0
    private var w: Int = 0

    private var isPopupOpenAccept = false
    private var isPopupOpenDecline = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.messages_list_fragment, container, false)
        halfWidth(view)
        if(savedInstanceState?.getBoolean("isOpen") == true)
            popUpAccept()
        if(savedInstanceState?.getBoolean("isOpenDecline") == true)
            popUpReject()
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
         *  4. Automatic refusal for other requester on the same adv
         * */
        accept.setOnClickListener{
            popUpAccept()
        }

        /** Deny for the current requester */
        deny.setOnClickListener {
            popUpReject()
        }
    }

    private fun popUpAccept(){
        isPopupOpenAccept = true
        alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Warning!")
            .setMessage("Accepting the request made by ${vmMessage.otherUserEmail} will automatically trigger" +
                    " the refusal of all the other requests you received on this advertisement.\n" +
                    "Do you want to continue?")
            .setPositiveButton("Yes") { _, _ ->
                vmTimeSlot.currentShownAdv?.available = 0
                vmTimeSlot.currentShownAdv?.accepted = vmMessage.otherUserEmail
                vmTimeSlot.currentShownAdv?.let { it1 -> vmTimeSlot.updateAdv(it1) }

                updateUserProfile()
                vmMessage.modifyProfileInChat(vmMessage.otherUserEmail, vmTimeSlot.currentShownAdv?.duration)

                isPopupOpenAccept = false
                val acceptedDefaultMessage = "I have accepted your request and also received the credit.\n" +
                        "We will meet on ${vmTimeSlot.currentShownAdv?.date}.\nThank you!"
                vmMessage.sendNewMessage(acceptedDefaultMessage)

                val rejectedEmail = vmChat.currentChatReceivedList.value
                    ?.filter{
                        it.info.split(",")[0] == vmTimeSlot.currentShownAdv?.id
                                && it.info.split(",")[1] != vmMessage.otherUserEmail
                    }?.map{ it.info.split(",")[1] }
                rejectedEmail?.forEach { vmMessage.sendAutoRejectMessage("Rejected", it) }
                requireActivity().onBackPressed()
            }
            .setNegativeButton("No") { _, _ ->
                isPopupOpenAccept = false
            }
            .show()

        alertDialog.setOnDismissListener { isPopupOpenAccept = false }
    }

    private fun updateUserProfile(){
        var item1= vmProfile.profile.value?.totatl_time?.split(":")?.toTypedArray()
        var sxItem1 = item1?.get(0)?.removeSuffix("h")
        var dxItem1 = item1?.get(1)?.removeSuffix("m")

        var item2 = vmTimeSlot.currentShownAdv?.duration?.split(":")?.toTypedArray()
        var sxItem2 = item2?.get(0)?.removeSuffix("h")
        var dxItem2 = item2?.get(1)?.removeSuffix("m")

        var m1 = (sxItem1?.toInt()!! * 60) + dxItem1!!.toInt()
        var m2 = (sxItem2?.toInt()!! * 60) + dxItem2!!.toInt()

        var minus = m1 + m2
        Log.d("hm", minus.toString())

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
        vmProfile.modifyUserProfile(
            ProfileUser(vmProfile.profile?.value?.img,
                vmProfile.profile?.value?.fullName,vmProfile.profile?.value?.nickname,vmProfile.profile?.value?.email,vmProfile.profile?.value?.location,
                vmProfile.profile?.value?.aboutUser, vmProfile.profile?.value!!.skills,h.toString() + "h" + ":" + m.toString() + "m"))
    }


    private fun popUpReject(){
        isPopupOpenDecline = true
        alertDecline = AlertDialog.Builder(requireContext())
            .setTitle("Warning!")
            .setMessage("Do you want to decline ${vmMessage.otherUserEmail} request?")
            .setPositiveButton("Yes") { _, _ ->
                vmTimeSlot.currentShownAdv?.refused = vmMessage.otherUserEmail
                vmTimeSlot.currentShownAdv?.let { it1 -> vmTimeSlot.updateAdv(it1) }
                isPopupOpenDecline = false
            }
            .setNegativeButton("No") { _, _ ->
                isPopupOpenDecline = false
            }
            .show()

        alertDecline.setOnDismissListener { isPopupOpenDecline = false }
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isOpen", isPopupOpenAccept)
        outState.putBoolean("isOpenDecline", isPopupOpenDecline)
    }
}