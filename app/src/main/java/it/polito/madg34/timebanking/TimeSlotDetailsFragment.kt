package it.polito.madg34.timebanking

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class TimeSlotDetailsFragment : Fragment() {

    val vm by viewModels<TimeSlotViewModel>()
    private  var bundle : Bundle = Bundle();

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.timeslotdetailsfragment_layout, container, false)
        val toolB = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar2)
        toolB.inflateMenu(R.menu.pencil_menu)
        toolB.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val title = view.findViewById<TextView>(R.id.title_slot)
        val description = view.findViewById<TextView>(R.id.description_slot)
        val date = view.findViewById<TextView>(R.id.date_slot)
        val time = view.findViewById<TextView>(R.id.time_slot)
        val duration = view.findViewById<TextView>(R.id.duration_slot)
        val location = view.findViewById<TextView>(R.id.location_slot)

        vm.title_vm.observe(this.viewLifecycleOwner){
            title.text = "$it"
        }

        vm.description_vm.observe(this.viewLifecycleOwner){
            description.text = "$it"
        }
        vm.date_vm.observe(this.viewLifecycleOwner){
            date.text = "$it"
        }
        vm.time_vm.observe(this.viewLifecycleOwner){
            time.text = "$it"
        }
        vm.duration_vm.observe(this.viewLifecycleOwner){
            duration.text = "$it"
        }

        vm.location_vm.observe(this.viewLifecycleOwner){
            location.text = "$it"
        }

        bundle.putString("title", vm.title_vm.toString())
        bundle.putString("description", vm.description_vm.toString())
        bundle.putString("date", vm.date_vm.toString())
        bundle.putString("time", vm.time_vm.toString())
        bundle.putString("duration", vm.duration_vm.toString())
        bundle.putString("location", vm.location_vm.toString())





    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}