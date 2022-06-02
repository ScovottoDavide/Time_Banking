package it.polito.madg34.timebanking.TimeSlots

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Messages.MessagesViewModel
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.HomeSkills.SkillsViewModel
import it.polito.madg34.timebanking.Chat.Chat
import it.polito.madg34.timebanking.Chat.ChatViewModel

class TimeSlotAdapter(val data: MutableList<TimeSlot>) :
    RecyclerView.Adapter<TimeSlotViewHolder>() {

    lateinit var v: View
    lateinit var vmTimeSlot: TimeSlotViewModel
    lateinit var vmSkills: SkillsViewModel
    lateinit var vmProfile: ProfileViewModel
    lateinit var vmChat: ChatViewModel
    lateinit var vmMessages: MessagesViewModel

    lateinit var dialog: AlertDialog
    lateinit var dialogChat: AlertDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return TimeSlotViewHolder(v)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        vmTimeSlot = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmSkills = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmProfile = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmChat = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmMessages = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        val item = data[position] // access data item
        vmTimeSlot.getImageFromEmail(item.published_by).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("IMG", vmTimeSlot.userUri)
                holder.bind(item, vmTimeSlot.userUri)
            }
        }

        val a = holder.itemView.context as AppCompatActivity
        val b =
            a.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = b.navController

        if (vmSkills.viewProfilePopupOpen) {
            showPopUpDialog(holder, item, navController)
        }

        // Click Listener on the whole Card
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("index", position)
            vmTimeSlot.currentShownAdv = data[position]
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment2, bundle)
        }

        val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.deleteCard)
        if (vmSkills.fromHome.value!!) {
            deleteButton.visibility = View.INVISIBLE
        } else {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                vmTimeSlot.removeAdv(data[position])
                data.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
                Snackbar.make(
                    holder.itemView,
                    "Service successfully removed!",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        val editCardViewButton: ImageButton = holder.itemView.findViewById(R.id.editCard)
        if (vmSkills.fromHome.value!!) {
            editCardViewButton.visibility = View.INVISIBLE
        } else {
            editCardViewButton.visibility = View.VISIBLE
            // Click Listener on the edit Button of the Card
            editCardViewButton.setOnClickListener {
                vmTimeSlot.currentShownAdv = data[position]
                holder.itemView.findNavController().navigate(
                    R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                    bundleOf("index" to position)
                )
            }
        }

        val chat = holder.itemView.findViewById<ImageButton>(R.id.chatButton)
        if (vmSkills.fromHome.value!!) {
            if (item.published_by != FirestoreRepository.currentUser.email) {
                chat.visibility = View.VISIBLE
                chat.setOnClickListener {
                    val newChat = Chat("${item.id},${item.published_by}")
                    if (vmChat.stringChat.contains(newChat.info)) {
                        vmMessages.currentRelatedAdv = item.id
                        vmMessages.otherUserEmail = item.published_by
                        vmMessages.loadMessages()
                        navController.navigate(R.id.action_timeSlotListFragment_to_messageFragment)
                    } else {
                       startNewChat(holder, item, newChat, navController)
                    }
                }
            } else {
                chat.visibility = View.GONE
            }
        } else {
            chat.visibility = View.GONE
        }

        val viewProfileButton: Button = holder.itemView.findViewById(R.id.profileButton)
        if (vmSkills.fromHome.value!!) {
            viewProfileButton.visibility = View.VISIBLE
            viewProfileButton.setOnClickListener {
                showPopUpDialog(holder, item, navController)
            }
        } else
            viewProfileButton.visibility = View.INVISIBLE
    }

    override fun getItemCount(): Int = data.size

    /** Time credit control has to be done after !!!! */
    private fun controlHour(item: String): Boolean {
        val item1 = item.split(":").toTypedArray()
        val sxItem1 = item1[0].removeSuffix("h")
        val dxItem1 = item1[1].removeSuffix("m")

        val item2 = vmProfile.getDBUser().value?.total_time?.split(":")?.toTypedArray()
        val sxItem2 = item2?.get(0)?.removeSuffix("h")
        val dxItem2 = item2?.get(1)?.removeSuffix("m")

        if (sxItem2?.toInt()!! > sxItem1.toInt()) {
            return true
        } else if (sxItem2.toInt() == sxItem1.toInt()) {
            return dxItem2?.toInt()!! >= dxItem1.toInt()
        } else {
            return false
        }
    }

    private fun startNewChat(
        holder: TimeSlotViewHolder,
        item: TimeSlot,
        newChat : Chat,
        navController: NavController
    ){
        vmChat.startNewChatPopUpOpen = true
        dialogChat = AlertDialog.Builder(holder.itemView.context)
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
                    navController.navigate(R.id.action_timeSlotListFragment_to_messageFragment)
                }
            }
            .setNegativeButton("No") { _, _ ->
                vmSkills.viewProfilePopupOpen = false
            }
            .show()
        dialogChat.setOnDismissListener { vmChat.startNewChatPopUpOpen = false }
    }

    private fun showPopUpDialog(
        holder: TimeSlotViewHolder,
        item: TimeSlot,
        navController: NavController
    ) {
        vmSkills.viewProfilePopupOpen = true
        dialog = AlertDialog.Builder(holder.itemView.context)
            .setTitle("Message")
            .setMessage("Do you want to visit ${item.published_by} profile? ")
            .setPositiveButton("Yes") { _, _ ->
                vmProfile.clickedEmail.value = item.published_by
                vmSkills.fromHome.value = true
                vmSkills.viewProfilePopupOpen = false
                navController.navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
            }
            .setNegativeButton("No") { _, _ ->
                vmSkills.viewProfilePopupOpen = false
            }
            .show()
        dialog.setOnDismissListener { vmSkills.viewProfilePopupOpen = false }
    }
}