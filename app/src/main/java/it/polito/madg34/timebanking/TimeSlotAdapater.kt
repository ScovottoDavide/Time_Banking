package it.polito.madg34.timebanking

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import it.polito.madg34.timebanking.Messages.MessagesViewModel
import it.polito.madg34.timebanking.chat.Chat
import it.polito.madg34.timebanking.chat.ChatViewModel

class TimeSlotAdapter(val data: MutableList<TimeSlot>) :
    RecyclerView.Adapter<TimeSlotViewHolder>() {

    lateinit var v: View
    lateinit var vmTimeSlot: TimeSlotViewModel
    lateinit var vmSkills: SkillsViewModel
    lateinit var vmProfile: ProfileViewModel
    lateinit var vmChat: ChatViewModel
    lateinit var vmMessages: MessagesViewModel

    lateinit var dialog: AlertDialog

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
        val b = a.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
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
                    if(vmChat.stringChat.contains(newChat.info)){
                        vmMessages.currentRelatedAdv = item.id
                        vmMessages.otherUserEmail = item.published_by
                        vmMessages.loadMessages()
                        navController.navigate(R.id.action_timeSlotListFragment_to_messageFragment)
                    }else {
                        vmChat.newChatAdd(newChat)?.addOnSuccessListener {
                            vmMessages.currentRelatedAdv = item.id
                            vmMessages.otherUserEmail = item.published_by
                            vmMessages.loadMessages()
                            navController.navigate(R.id.action_timeSlotListFragment_to_messageFragment)
                        }
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