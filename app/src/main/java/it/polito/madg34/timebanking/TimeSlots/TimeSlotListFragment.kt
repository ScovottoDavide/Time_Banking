package it.polito.madg34.timebanking.TimeSlots

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.HomeSkills.SkillsViewModel
import java.text.SimpleDateFormat
import java.util.*


class TimeSlotListFragment : Fragment() {

    val vm: TimeSlotViewModel by activityViewModels()
    val vmSkills: SkillsViewModel by activityViewModels()


    private var timeSlots: MutableList<TimeSlot> = mutableListOf()
    private var timeSlotsFromSkill: MutableList<TimeSlot> = mutableListOf()

    lateinit var timeSlotsRV: RecyclerView
    lateinit var emptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeslotlistfragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addButton: FloatingActionButton = view.findViewById(R.id.add_button)
        val fromSkillTitle : TextView = view.findViewById(R.id.chatTitle)

        vmSkills.fromHome.observe(viewLifecycleOwner) { fromHome ->
            if (fromHome) {
                addButton.hide()
                vmSkills.getAdvsToDisplayFromSkill().observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        timeSlotsFromSkill = it.filter{timeSlot ->  timeSlot.available==1} as MutableList<TimeSlot>
                    }
                    if (timeSlotsFromSkill.size == 0) {
                        emptyView = view.findViewById(R.id.emptyListTV)
                        emptyView.visibility = View.VISIBLE
                    } else {
                        emptyView = view.findViewById(R.id.emptyListTV)
                        emptyView.visibility = View.GONE
                        fromSkillTitle.setText(getString(R.string.ListOnlineServices))
                        timeSlotsRV = view.findViewById(R.id.ServicesList)
                        timeSlotsRV.layoutManager = LinearLayoutManager(this.context)
                        vm.filtered.observe(viewLifecycleOwner){
                            if(it)
                                timeSlotsRV.adapter = TimeSlotAdapter(vm.filteredTimeSlots)
                            else{
                                timeSlotsRV.adapter = TimeSlotAdapter(timeSlotsFromSkill)
                            }
                        }
                    }
                }
            } else {
                vm.getDBTimeSlots().observe(viewLifecycleOwner) {
                    if (!it.isNullOrEmpty()) {
                        timeSlots = it as MutableList<TimeSlot>
                    }
                    if (timeSlots.size == 0) {
                        emptyView = view.findViewById(R.id.emptyListTV)
                        emptyView.visibility = View.VISIBLE
                        addButton.setOnClickListener {
                            findNavController().navigate(
                                R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                                bundleOf("index" to -1)
                            )
                        }
                    } else {
                        emptyView = view.findViewById(R.id.emptyListTV)
                        emptyView.visibility = View.GONE
                        timeSlotsRV = view.findViewById(R.id.ServicesList)
                        timeSlotsRV.layoutManager = LinearLayoutManager(this.context)
                        timeSlotsRV.adapter = TimeSlotAdapter(timeSlots)
                        addButton.setOnClickListener {
                            findNavController().navigate(
                                R.id.action_timeSlotListFragment_to_timeSlotEditFragment2,
                                bundleOf("index" to (timeSlots.size.plus(1)))
                            )
                        }
                    }
                }
            }

        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vmSkills.fromHome.value = false
                    if (isEnabled) {
                        isEnabled = false
                        vm.filtered.value = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (vmSkills.fromHome.value!!){
            inflater.inflate(R.menu.homepage_menu, menu)
            // Associate searchable configuration with the SearchView
            val searchManager = requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val search = menu.findItem(R.id.search).actionView as SearchView
            search.isIconifiedByDefault = false
            search.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    val filteredList = newText?.let { filter(timeSlotsFromSkill, it) }
                    timeSlotsRV.adapter = TimeSlotAdapter(filteredList as MutableList<TimeSlot>)
                    timeSlotsRV.adapter?.notifyDataSetChanged()
                    return true
                }
            })
        }
        return super.onCreateOptionsMenu(menu, inflater)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                val popupMenu = PopupMenu(context, view?.findViewById(R.id.anchor))
                popupMenu.menuInflater.inflate(R.menu.filter_menu_timeslotlist, popupMenu.menu)
                watchFilterMenu(popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    if (item != null) {
                        when (item.itemId) {
                            R.id.sort -> {
                                vmSkills.selection.value = item.itemId
                                sortByTitle()
                                vm.filtered.value = true
                                timeSlotsRV.adapter?.notifyDataSetChanged()
                                true
                            }
                            R.id.date -> {
                                vmSkills.selection.value = item.itemId
                                filterByDate()
                                true
                            }
                            R.id.nothing -> {
                                vmSkills.selection.value = item.itemId
                                vm.filtered.value = false
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
            if (item.location.lowercase().contains(newText.lowercase()) || item.title.lowercase().contains(newText.lowercase())) {
                filteredList.add(item)
            }
        }
        return filteredList
    }

    private fun sortByTitle() {
        if(vm.filtered.value!! ){
            vm.filteredTimeSlots.sortBy { it.title.lowercase() }
        }else {
            vm.filteredTimeSlots = mutableListOf()
            timeSlotsFromSkill.forEach { vm.filteredTimeSlots.add(it) }
            vm.filteredTimeSlots.sortBy { it.title.lowercase() }
        }

    }

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
            vm.filteredTimeSlots = mutableListOf()
            timeSlotsFromSkill.forEach {
                Log.d("DATEE", "it ${it.date}, formatted $formatted")
                if(it.date == formatted)
                    vm.filteredTimeSlots.add(it)
            }
            vm.filtered.value = true
            timeSlotsRV.adapter?.notifyDataSetChanged()
        }
    }

    private fun watchFilterMenu(menu : Menu){
        vmSkills.selection.observe(viewLifecycleOwner){
            when(it){
                R.id.sort -> {
                    val menuItem = menu.findItem(R.id.sort)
                    menuItem.isChecked = true
                }
                R.id.date -> {
                    val menuItem = menu.findItem(R.id.date)
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