package it.polito.madg34.timebanking.Chat

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.HomeSkills.Skills
import it.polito.madg34.timebanking.HomeSkills.SkillsAdapter
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {
    val vmChat: ChatViewModel by activityViewModels()
    val vmTimeSlot: TimeSlotViewModel by activityViewModels()

    private var chatList: List<Chat> = listOf()
    private var chatReceivedList: List<Chat> = listOf()
    private var timeSlots: MutableList<TimeSlot> = mutableListOf()
    private var timeSlotsIds: MutableList<String> = mutableListOf()

    lateinit var chatRV: RecyclerView
    lateinit var titlePage: TextView
    lateinit var emptyChat: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat_list_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titlePage = view.findViewById(R.id.chatTitle)
        emptyChat = view.findViewById(R.id.emptyChat)

        vmChat.sentOrReceived.observe(viewLifecycleOwner) { sentOrreceived ->
            if (sentOrreceived) { // received
                titlePage.setText("Incoming Requests")
                vmChat.getCurrentChatReceivedList().observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        emptyChat.visibility = View.GONE
                        chatReceivedList = it
                        chatReceivedList.forEach {
                            it.info.split("|").forEach { s ->
                                timeSlotsIds.add(s.split(",")[0])
                            }
                        }
                        vmTimeSlot.loadAdvByIds(timeSlotsIds)
                        vmTimeSlot.getChatTimeSlots().observe(viewLifecycleOwner) { it1 ->
                            if (it1 != null) {
                                timeSlots = it1
                                chatRV = view.findViewById(R.id.ChatList)
                                chatRV.layoutManager = LinearLayoutManager(this.context)
                                vmChat.filtered.observe(viewLifecycleOwner) { filtered ->
                                    if (filtered)
                                        if (vmChat.filteredTimeSlots.isEmpty())
                                            chatRV.adapter = ChatAdapter(
                                                emptyList(),
                                                emptyList(),
                                                viewLifecycleOwner
                                            )
                                        else
                                            chatRV.adapter = ChatAdapter(
                                                vmChat.filteredChat,
                                                vmChat.filteredTimeSlots,
                                                viewLifecycleOwner
                                            )
                                    else
                                        chatRV.adapter = ChatAdapter(
                                            chatReceivedList,
                                            timeSlots,
                                            viewLifecycleOwner
                                        )
                                }
                            }
                        }
                    } else {
                        emptyChat.setText(getString(R.string.chatReceived))
                        emptyChat.visibility = View.VISIBLE
                    }
                }
                if (chatReceivedList.isEmpty()) {
                    emptyChat.setText(getString(R.string.chatReceived))
                    emptyChat.visibility = View.VISIBLE
                } else {
                    emptyChat.visibility = View.GONE
                }
            } else {
                titlePage.setText("Outgoing Requests")
                vmChat.getCurrentChatList().observe(viewLifecycleOwner) { it1 ->
                    if (!it1.isNullOrEmpty()) {
                        emptyChat.visibility = View.GONE
                        chatList = it1
                        chatList.forEach { c ->
                            c.info.split("|").forEach { s ->
                                timeSlotsIds.add(s.split(",")[0])
                            }
                        }
                        vmTimeSlot.loadAdvByIds(timeSlotsIds)
                        vmTimeSlot.getChatTimeSlots().observe(viewLifecycleOwner) { it2 ->
                            if (it2 != null) {
                                timeSlots = it2
                                chatRV = view.findViewById(R.id.ChatList)
                                chatRV.layoutManager = LinearLayoutManager(this.context)
                                vmChat.filtered.observe(viewLifecycleOwner) { filtered ->
                                    if (filtered)
                                        if (vmChat.filteredTimeSlots.isEmpty())
                                            chatRV.adapter = ChatAdapter(
                                                emptyList(),
                                                emptyList(),
                                                viewLifecycleOwner
                                            )
                                        else
                                            chatRV.adapter = ChatAdapter(
                                                vmChat.filteredChat,
                                                vmChat.filteredTimeSlots,
                                                viewLifecycleOwner
                                            )
                                    else
                                        chatRV.adapter =
                                            ChatAdapter(chatList, timeSlots, viewLifecycleOwner)
                                }
                            }
                        }
                    } else {
                        emptyChat.setText(getString(R.string.chatSent))
                        emptyChat.visibility = View.VISIBLE
                    }
                }
                if (chatList.isEmpty()) {
                    emptyChat.setText(getString(R.string.chatSent))
                    emptyChat.visibility = View.VISIBLE
                } else {
                    emptyChat.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.homepage_menu, menu)
        // Associate searchable configuration with the SearchView
        val searchManager =
            requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val search = menu.findItem(R.id.search).actionView as SearchView
        search.isIconifiedByDefault = false
        search.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = newText?.let { filter(timeSlots, it) }
                if (vmChat.sentOrReceived.value == true) { //received
                    if (filteredList.isNullOrEmpty())
                        chatRV.adapter = ChatAdapter(emptyList(), emptyList(), viewLifecycleOwner)
                    else
                        chatRV.adapter =
                            ChatAdapter(chatReceivedList, filteredList, viewLifecycleOwner)
                    chatRV.adapter?.notifyDataSetChanged()
                } else {
                    if (filteredList.isNullOrEmpty())
                        chatRV.adapter = ChatAdapter(emptyList(), emptyList(), viewLifecycleOwner)
                    else
                        chatRV.adapter = ChatAdapter(chatList, filteredList, viewLifecycleOwner)
                    chatRV.adapter?.notifyDataSetChanged()
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                val popupMenu = PopupMenu(context, view?.findViewById(R.id.anchor))
                popupMenu.menuInflater.inflate(R.menu.filter_menu_chat, popupMenu.menu)
                watchFilterMenu(popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    if (item != null) {
                        when (item.itemId) {
                            R.id.date -> {
                                if(chatList.isNotEmpty() || chatReceivedList.isNotEmpty()){
                                    vmChat.selection.value = item.itemId
                                    filterByDate()
                                }
                                true
                            }
                            R.id.accepted -> {
                                if(chatList.isNotEmpty() || chatReceivedList.isNotEmpty()){
                                    vmChat.selection.value = item.itemId
                                    filterAccepted()
                                    vmChat.filtered.value = true
                                    chatRV.adapter?.notifyDataSetChanged()
                                }
                                true
                            }
                            R.id.rejected -> {
                                if(chatList.isNotEmpty() || chatReceivedList.isNotEmpty()){
                                    vmChat.selection.value = item.itemId
                                    filterRejected()
                                    vmChat.filtered.value = true
                                    chatRV.adapter?.notifyDataSetChanged()
                                }
                                true
                            }
                            R.id.nothing -> {
                                vmChat.selection.value = item.itemId
                                vmChat.filtered.value = false
                                true
                            }
                            else -> super.onOptionsItemSelected(item)
                        }
                    } else {
                        false
                    }
                })
                popupMenu.show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    // filters the existing list that's provided to the List Adapter
    private fun filter(mList: List<TimeSlot>, newText: String): List<TimeSlot> {
        val filteredList: MutableList<TimeSlot> = ArrayList()
        for (item in mList) {
            if (item.location.lowercase().contains(newText.lowercase()) || item.title.lowercase()
                    .contains(newText.lowercase())
            ) {
                filteredList.add(item)
            }
        }
        return filteredList
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterByDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.show(this.parentFragmentManager, "")
        datePicker.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.ITALY)
            val formatted: String = format.format(utc.time)
            vmChat.filteredTimeSlots = mutableListOf()
            vmChat.filteredChat = mutableListOf()
            timeSlots.forEach { t ->
                if (t.date == formatted)
                    vmChat.filteredTimeSlots.add(t)
            }
            vmChat.filteredTimeSlots.forEach { t ->
                vmChat.filteredChat.add(Chat("${t.id},${t.published_by}"))
            }
            vmChat.filtered.value = true
            chatRV.adapter?.notifyDataSetChanged()
        }
    }

    private fun filterAccepted() {
        vmChat.filteredTimeSlots = mutableListOf()
        vmChat.filteredChat = mutableListOf()
        if (vmChat.sentOrReceived.value!!) {
            val tmp = timeSlots.filter { it.accepted.isNotEmpty() }.distinct()
            if (tmp.isEmpty())
                vmChat.filteredTimeSlots = mutableListOf()
            else
                vmChat.filteredTimeSlots = tmp.toMutableList()
            vmChat.filteredTimeSlots.forEach {
                vmChat.filteredChat.add(Chat("${it.id},${it.accepted}"))
            }
        } else {
            val tmp = timeSlots.filter { it.accepted == FirestoreRepository.currentUser.email!! }
                .distinct()
            if (tmp.isEmpty())
                vmChat.filteredTimeSlots = mutableListOf()
            else
                vmChat.filteredTimeSlots = tmp.toMutableList()
            vmChat.filteredTimeSlots.forEach {
                vmChat.filteredChat.add(Chat("${it.id},${it.published_by}"))
            }
        }
    }

    private fun filterRejected() {
        vmChat.filteredTimeSlots = mutableListOf()
        vmChat.filteredChat = mutableListOf()
        if (vmChat.sentOrReceived.value!!) {
            val tmp = timeSlots.filter { it.refused.isNotEmpty() }.distinct()
            if (tmp.isEmpty())
                vmChat.filteredTimeSlots = mutableListOf()
            else
                vmChat.filteredTimeSlots = tmp.toMutableList()
            vmChat.filteredTimeSlots.forEach {
                val splitReject = it.refused.split(",")
                splitReject.forEach { s ->
                    vmChat.filteredChat.add(Chat("${it.id},${s}"))
                }
            }
        } else {
            val tmp =
                timeSlots.filter { it.refused.contains(FirestoreRepository.currentUser.email!!) }
                    .distinct()
            if (tmp.isEmpty())
                vmChat.filteredTimeSlots = mutableListOf()
            else
                vmChat.filteredTimeSlots = tmp.toMutableList()
            vmChat.filteredTimeSlots.forEach {
                vmChat.filteredChat.add(Chat("${it.id},${it.published_by}"))
            }
        }
    }

    private fun watchFilterMenu(menu: Menu) {
        vmChat.selection.observe(viewLifecycleOwner) {
            when (it) {
                R.id.date -> {
                    val menuItem = menu.findItem(R.id.date)
                    menuItem.isChecked = true
                }
                R.id.accepted -> {
                    val menuItem = menu.findItem(R.id.accepted)
                    menuItem.isChecked = true
                }
                R.id.rejected -> {
                    val menuItem = menu.findItem(R.id.rejected)
                    menuItem.isChecked = true
                }
                R.id.nothing -> {
                    val menuItem = menu.findItem(R.id.nothing)
                    menuItem.isChecked = true
                }
            }
        }
    }
}