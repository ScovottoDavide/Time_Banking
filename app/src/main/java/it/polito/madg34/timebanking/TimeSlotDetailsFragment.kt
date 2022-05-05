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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class TimeSlotDetailsFragment : Fragment() {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private var h: Int = 0
    private var w: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.timeslotdetailsfragment_layout, container, false)
        setHasOptionsMenu(true)
        halfWidth(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)

        val bundle : Bundle? = arguments
        val item : TimeSlot? = vm.listServices.value?.get(bundle?.getInt("index")!!)

        val title = view.findViewById<TextInputEditText>(R.id.outlinedTitleFixed)
        val description = view.findViewById<TextInputEditText>(R.id.outlinedDescriptionFixed)
        val date = view.findViewById<TextInputEditText>(R.id.outlinedDateFixed)
        val time = view.findViewById<TextInputEditText>(R.id.outlinedTimeFixed)
        val duration = view.findViewById<TextInputEditText>(R.id.outlinedDurationFixed)
        val location = view.findViewById<TextInputEditText>(R.id.outlinedLocationFixed)

        // Observe in order to get automatically the updated values
        vm.listServices.observe(this.viewLifecycleOwner){
            title.setText(item?.title)
            description.setText(item?.description)
            date.setText(item?.date)
            time.setText(item?.time)
            duration.setText(item?.duration)
            location.setText(item?.location)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                vm.saveServices(vm.listServices.value!!)
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pencil_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                val bundle = arguments
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment, bundle)
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