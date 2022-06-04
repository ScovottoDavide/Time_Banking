package it.polito.madg34.timebanking.Chat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentSnapshot
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.HomeSkills.SkillsViewModel
import it.polito.madg34.timebanking.Messages.MessagesViewModel
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel

class ChatAdapter(
    val chatList: List<Chat>,
    val timeSlots: List<TimeSlot>,
    val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<ChatViewHolder>() {
    lateinit var v: View

    lateinit var vmProfile: ProfileViewModel
    lateinit var vmMessages: MessagesViewModel
    lateinit var vmChat: ChatViewModel
    lateinit var vmTimeSlot: TimeSlotViewModel
    lateinit var vmSkills: SkillsViewModel

    lateinit var chatButton: ImageButton
    lateinit var topCardTitle: TextView
    lateinit var unreadMessages: TextView
    lateinit var dialog: AlertDialog
    lateinit var reviewButton: MaterialButton

    lateinit var reviewDialog: AlertDialog

    var rateValue = 0f

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
        unreadMessages = holder.itemView.findViewById(R.id.tv_nav_drawer_count)
        reviewButton = holder.itemView.findViewById(R.id.reviewButton)

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
                if (it != null)
                    if (timeSlot != null)
                        holder.bind(timeSlot, vmProfile.chatImage.value, chatEntryEmail)
            }
            val text = "<a href=''> ${chatEntryEmail}</a> contacted you"
            topCardTitle.setText(Html.fromHtml(text))
            topCardTitle.setOnClickListener {
                viewProfile(holder, chatEntryEmail, navController)
            }
            if (timeSlot?.accepted == chatEntryEmail) {
                reviewButton.visibility = View.VISIBLE
            } else {
                reviewButton.visibility = View.GONE
            }
        } else {
            profileImageReceived = chatEntryEmail
            vmProfile.loadChatImage(profileImageReceived).addOnSuccessListener {
                if (it != null)
                    if (timeSlot != null)
                        holder.bind(
                            timeSlot,
                            vmProfile.chatImage.value,
                            FirestoreRepository.currentUser.email!!
                        )
            }
            topCardTitle.setText("You contacted ${profileImageReceived}")
            if (timeSlot?.accepted == FirestoreRepository.currentUser.email) {
                reviewButton.visibility = View.VISIBLE
            } else {
                reviewButton.visibility = View.GONE
            }
        }

        vmMessages.getAllMessages().observe(lifecycleOwner) {
            val unread = it.filter {
                it.relatedAdv == chatEntryAdv && it.read == 0 && it.receivedBy == FirestoreRepository.currentUser.email!!
                        && it.sentBy == chatEntryEmail
            }.size
            if (unread == 0) {
                unreadMessages.visibility = View.GONE
            } else {
                unreadMessages.visibility = View.VISIBLE
                unreadMessages.setText("$unread")
            }
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

        reviewButton.setOnClickListener {
            if(timeSlot!!.reviews.contains(timeSlot.accepted) && vmChat.sentOrReceived.value!!){
                Snackbar.make(holder.itemView, "You have alread sent a review!", Snackbar.LENGTH_SHORT).show()
            }else if(timeSlot.reviews.contains(timeSlot.published_by) && vmChat.sentOrReceived.value == false){
                Snackbar.make(holder.itemView, "You have already sent a review!", Snackbar.LENGTH_SHORT).show()
            }else{
                ratingPopup(holder, timeSlot)
            }
        }
    }

    fun ratingPopup(holder: ChatViewHolder, timeS: TimeSlot) {
        val reviewDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
        val view =
            LayoutInflater.from(holder.itemView.context).inflate(R.layout.rating_popup, null, false)

        val rb: RatingBar = view.findViewById(R.id.ratingBar)
        rb.setOnRatingBarChangeListener(RatingBar.OnRatingBarChangeListener { ratingBar, fl, b ->
            rateValue = fl
        })

        val button: Button = view.findViewById(R.id.btnSubRating)
        button.setOnClickListener {
            if(vmChat.sentOrReceived.value!!){
                updateProfileReview(timeS.accepted, rateValue, vmChat.sentOrReceived.value!!).addOnSuccessListener {
                    if(timeS.reviews.isEmpty()){
                        timeS.reviews = timeS.accepted
                    }else{
                        timeS.reviews = "${timeS.reviews},${timeS.accepted}"
                    }
                    vmTimeSlot.updateAdv(timeS)
                }
            }else{
                updateProfileReview(timeS.published_by, rateValue, vmChat.sentOrReceived.value!!).addOnSuccessListener {
                    if(timeS.reviews.isEmpty()){
                        timeS.reviews = timeS.published_by
                    }else{
                        timeS.reviews = "${timeS.reviews},${timeS.published_by}"
                    }
                    vmTimeSlot.updateAdv(timeS)
                }
            }
            reviewDialog.dismiss()
        }

        reviewDialogBuilder.setView(view)
        reviewDialog = reviewDialogBuilder.create()
        reviewDialog.show()
    }

    fun updateProfileReview(email: String, value: Float, which: Boolean): Task<DocumentSnapshot> {
        return vmProfile.updateUserReview(email, value, which).addOnSuccessListener {
            val messageContent = "I have sent you a review based on my experience with you.\n" +
                    "I gave you a score of $value. Thank you for your collaboration\n" +
                    "and hope we will meet in other occasions."
            vmMessages.sendAutoRejectMessage(messageContent, email)
        }
    }

    fun viewProfile(holder: ChatViewHolder, chatEntryMail: String, navController: NavController) {
        vmChat.viewProfilePopupOpen = true
        dialog = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Message")
            .setMessage("Do you want to visit ${chatEntryMail} profile? ")
            .setPositiveButton("Yes") { _, _ ->
                vmChat.viewProfilePopupOpen = false
                vmProfile.clickedEmail.value = chatEntryMail
                vmSkills.fromHome.value = true
                navController.navigate(R.id.action_chatFragment_to_showProfileFragment)
            }
            .setNegativeButton("No") { _, _ ->
                vmChat.viewProfilePopupOpen = false
            }
            .show()
        dialog.setOnDismissListener {
            vmChat.viewProfilePopupOpen = false
        }
    }

    override fun getItemCount(): Int = chatList.size
}