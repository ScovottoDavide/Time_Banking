package it.polito.madg34.timebanking

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

class TimeSlotDetailsFragment : Fragment(R.layout.timeslotdetailsfragment_layout) {

    val vm by viewModels<TimeSlotViewModel>()

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


    }


}