package it.polito.madg34.timebanking.TimeSlots

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.TableRow
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import it.polito.madg34.timebanking.Chat.Chat
import it.polito.madg34.timebanking.Chat.ChatViewModel
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.HomeSkills.SkillsViewModel
import it.polito.madg34.timebanking.Messages.MessagesViewModel

class TimeSlotDetailsFragment : Fragment() {

    val vm: TimeSlotViewModel by activityViewModels()
    val vmSkills: SkillsViewModel by activityViewModels()
    val vmProfile: ProfileViewModel by activityViewModels()
    val vmChat: ChatViewModel by activityViewModels()
    val vmMessages : MessagesViewModel by activityViewModels()


    lateinit var dialogChat : AlertDialog
    private var h: Int = 0
    private var w: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeslotdetailsfragment_layout, container, false)
        setHasOptionsMenu(true)
        halfWidth(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)

        val item: TimeSlot? = vm.currentShownAdv

        val title = view.findViewById<TextInputEditText>(R.id.outlinedTitleFixed)
        val description = view.findViewById<TextInputEditText>(R.id.outlinedDescriptionFixed)
        val date = view.findViewById<TextInputEditText>(R.id.outlinedDateFixed)
        val time = view.findViewById<TextInputEditText>(R.id.outlinedTimeFixed)
        val duration = view.findViewById<TextInputEditText>(R.id.outlinedDurationFixed)
        val location = view.findViewById<TextInputEditText>(R.id.outlinedLocationFixed)
        val email = view.findViewById<TextInputEditText>(R.id.outlinedMailFixed)
        val skill = view.findViewById<TextInputEditText>(R.id.outlinedSkillFixed)
        val chat = view.findViewById<FloatingActionButton>(R.id.chat_button)

        // Observe in order to get automatically the updated values
        vm.getDBTimeSlots().observe(this.viewLifecycleOwner) {
            title.setText(item?.title)
            description.setText(item?.description)
            date.setText(item?.date)
            time.setText(item?.time)
            duration.setText(item?.duration)
            location.setText(item?.location)
            if(vmSkills.fromHome.value == true){
                val text = "<a href=''> ${item?.published_by} </a>"
                email.setText(Html.fromHtml(text))
            }else{
                email.setText(item?.published_by)
            }
            skill.setText(item?.related_skill)
        }

        if(vmSkills.fromHome.value!!){
            email.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Message")
                    .setMessage("Do you want to visit ${item?.published_by} profile? ")
                    .setPositiveButton("Yes") { _, _ ->
                        vmProfile.clickedEmail.value = item?.published_by.toString()
                        vmSkills.fromHome.value = true
                        findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_showProfileFragment)
                    }
                    .setNegativeButton("No") { _, _ ->
                    }
                    .show()
            }
            if(item?.published_by != FirestoreRepository.currentUser.email){
                chat.visibility = View.VISIBLE
            }else{
                chat.visibility = View.GONE
            }
        }

        chat.setOnClickListener{
            val newChat = Chat("${item!!.id},${item.published_by}")
            if (vmChat.stringChat.contains(newChat.info)) {
                vmMessages.currentRelatedAdv = item.id
                vmMessages.otherUserEmail = item.published_by
                vmMessages.loadMessages()
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_messageFragment)
            } else {
                startNewChat( item, newChat)
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    private fun startNewChat(
        item: TimeSlot,
        newChat : Chat,
    ){
        vmChat.startNewChatPopUpOpen = true
        dialogChat = AlertDialog.Builder(requireContext())
            .setTitle("Message")
            .setMessage("Do you want to send a new request for this offer to ${item.published_by}?\n"
                    +"A default message will be sent!")
            .setPositiveButton("Yes") { _, _ ->
                vmChat.startNewChatPopUpOpen = true
                vmChat.newChatAdd(newChat)?.addOnSuccessListener {
                    vmMessages.currentRelatedAdv = item.id
                    vmMessages.otherUserEmail = item.published_by
                    val acceptedDefaultMessage =
                        "Hi ${item.published_by} I'm interested on this offer.\n"
                    vmMessages.sendNewMessage(acceptedDefaultMessage)
                    vmMessages.loadMessages()
                    findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_messageFragment)
                }
            }
            .setNegativeButton("No") { _, _ ->
                vmSkills.viewProfilePopupOpen = false
            }
            .show()
        dialogChat.setOnDismissListener { vmChat.startNewChatPopUpOpen = false }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!vmSkills.fromHome.value!!)
            inflater.inflate(R.menu.pencil_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                val bundle = arguments
                findNavController().navigate(
                    R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment,
                    bundle
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun halfWidth(view: View) {
        val row = view.findViewById<TableRow>(R.id.RowDateTime)
        val date = view.findViewById<TextInputLayout>(R.id.outlinedDate)
        val time = view.findViewById<TextInputLayout>(R.id.outlinedTime)

        row.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    h = row.height
                    w = row.width
                    date.post { date.layoutParams = TableRow.LayoutParams(w / 2, h) }
                    time.post { time.layoutParams = TableRow.LayoutParams(w / 2, h) }
                } else {
                    h = row.height
                    w = row.width
                    date.post { date.layoutParams = TableRow.LayoutParams(w / 2, h) }
                    time.post { time.layoutParams = TableRow.LayoutParams(w / 2, h) }
                }
                row.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}