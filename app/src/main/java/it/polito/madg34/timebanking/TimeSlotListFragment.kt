package it.polito.madg34.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Adapter
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class TimeSlotListFragment : Fragment() {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private var timeSlots : MutableList<TimeSlot> = mutableListOf()

    lateinit var timeSlotsRV: RecyclerView
    lateinit var emptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeslotlistfragment_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addButton: FloatingActionButton = view.findViewById(R.id.add_button)
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