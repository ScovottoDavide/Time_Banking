package it.polito.madg34.timebanking

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView


class TimeSlotListFragment : Fragment() {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    lateinit var timeSlotsRV: RecyclerView
    lateinit var emptyView : TextView

   /*private val services: MutableList<TimeSlot> = mutableListOf(
       TimeSlot("uno", "prova", "04-05-2022", "00:06", "1 Hour", "Torino"),
       TimeSlot("due", "prova", "04-05-2022", "00:06", "1 Hour", "Napoli")
   )*/
    /*private val services: MutableList<TimeSlot> = mutableListOf(
        TimeSlot("", "", "", "", "", "")
    )*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.timeslotlistfragment_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vm._listServices.value = services
        //vm.saveServices(vm.listServices.value!!)

        if(vm.listServices.value?.size != null){
            timeSlotsRV = view.findViewById(R.id.ServicesList)
            timeSlotsRV.layoutManager = LinearLayoutManager(this.context)
            timeSlotsRV.adapter = TimeSlotAdapter(vm.listServices.value!!)

            val add_button: FloatingActionButton = view.findViewById(R.id.add_button)
            add_button.setOnClickListener{
                findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2, bundleOf("index" to (vm.listServices.value?.size!! + 1)))
            }
        } else {
            emptyView = view.findViewById(R.id.emptyListTV)
            emptyView.visibility = View.VISIBLE
            val add_button: FloatingActionButton = view.findViewById(R.id.add_button)
            add_button.setOnClickListener{
                findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2, bundleOf("index" to -1))
            }
        }
    }
}