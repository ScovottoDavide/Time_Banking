package it.polito.madg34.timebanking

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TimeSlotListFragment : Fragment() {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    lateinit var timeSlotsRV: RecyclerView
    lateinit var emptyView : TextView

   private val services: MutableList<TimeSlot> = mutableListOf(
        TimeSlot("uno", "prova", "04-05-2022", "00:06", "1 Hour", "Torino")
   )
    /*private val services: MutableList<TimeSlot> = mutableListOf(
        TimeSlot("", "", "", "", "", "")
    )*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.timeslotlistfragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm._listServices.value = services
        //vm.saveServices(vm.listServices.value!!)

        if(vm.listServices.value?.size != null){
            timeSlotsRV = view.findViewById(R.id.ServicesList)
            timeSlotsRV.layoutManager = LinearLayoutManager(this.context)
            timeSlotsRV.adapter = TimeSlotAdapter(vm.listServices.value!!)
        } else {
            emptyView = view.findViewById(R.id.emptyListTV)
            emptyView.visibility = View.VISIBLE
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pencil_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}