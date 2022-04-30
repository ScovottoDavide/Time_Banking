package it.polito.madg34.timebanking

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TimeSlotDetailsFragment : Fragment() {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private var h: Int = 0
    private var w: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.timeslotdetailsfragment_layout, container, false)
        val toolB = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar2)
        toolB.inflateMenu(R.menu.pencil_menu)
        toolB.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }
        halfWidth(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)
        val title = view.findViewById<TextInputEditText>(R.id.outlinedTitleFixed)
        val description = view.findViewById<TextInputEditText>(R.id.outlinedDescriptionFixed)
        val date = view.findViewById<TextInputEditText>(R.id.outlinedDateFixed)
        val time = view.findViewById<TextInputEditText>(R.id.outlinedTimeFixed)
        val duration = view.findViewById<TextInputEditText>(R.id.outlinedDurationFixed)
        val location = view.findViewById<TextInputEditText>(R.id.outlinedLocationFixed)

        vm.title_vm.observe(this.viewLifecycleOwner){
            title.setText(it)
            Log.d("TAG", title.text.toString())
        }

        vm.description_vm.observe(this.viewLifecycleOwner){
            description.setText(it)
        }
        vm.date_vm.observe(this.viewLifecycleOwner){
            date.setText(it)
        }
        vm.time_vm.observe(this.viewLifecycleOwner){
            time.setText(it)
        }
        vm.duration_vm.observe(this.viewLifecycleOwner){
            duration.setText(it)
        }

        vm.location_vm.observe(this.viewLifecycleOwner){
            location.setText(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun halfWidth(view: View){
        val row = view.findViewById<TableRow>(R.id.RowDateTime)
        val date = view.findViewById<TextInputLayout>(R.id.outlinedDate)
        val time = view.findViewById<TextInputLayout>(R.id.outlinedTime)

        row.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if(resources.configuration.orientation== Configuration.ORIENTATION_PORTRAIT){
                    h = row.height
                    w = row.width
                    date.post { date.layoutParams = TableRow.LayoutParams(w /2 , h) }
                    time.post { time.layoutParams = TableRow.LayoutParams(w /2 , h) }
                } else {
                    h = row.height
                    w = row.width
                    date.post { date.layoutParams = TableRow.LayoutParams(w / 2 , h) }
                    time.post { time.layoutParams = TableRow.LayoutParams(w / 2 , h) }
                }
                row.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

}