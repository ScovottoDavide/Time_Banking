package it.polito.madg34.timebanking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment: Fragment(R.layout.timesloteditfragment_layout) {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private var h: Int = 0
    private var w: Int = 0

    private lateinit var  _date : String;
    private lateinit var  _time : String;
    private lateinit var _title : String;
    private lateinit var _description : String;
    private lateinit var _duration : String;
    private lateinit var _location : String;

    private lateinit var title : TextInputEditText
    private lateinit var description : TextInputEditText
    private lateinit var duration : TextInputEditText
    private lateinit var location : TextInputEditText
    private lateinit var  date : TextInputLayout
    private lateinit var  time : TextInputLayout

    private var hour = 0;
    private var minute = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            _title = savedInstanceState.getString("title").toString()
            _description = savedInstanceState.getString("description").toString()
            _date = savedInstanceState.getString("date").toString()
            _time = savedInstanceState.getString("time").toString()
            _duration = savedInstanceState.getString("duration").toString()
            _location = savedInstanceState.getString("location").toString()

            vm.m_title(_title)
            vm.m_description(_description)
            vm.m_date(_date)
            vm.m_time(_time)
            vm.m_duration(_duration)
            vm.m_location(_location)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)
        title = view.findViewById(R.id.outlinedTitleFixed)
        description = view.findViewById(R.id.outlinedDescriptionFixed)
        duration = view.findViewById(R.id.outlinedDurationFixed)
        location = view.findViewById(R.id.outlinedLocationFixed)
        date = view.findViewById(R.id.outlinedDate)
        time = view.findViewById(R.id.outlinedTime)

        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener {_ , year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalendar)
        }

        val timerPicker = TimePickerDialog.OnTimeSetListener {_, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
            time.editText?.setText(String.format(Locale.getDefault(),"%02d:%02d", hour , minute))
        }


        date.setStartIconOnClickListener {
            DatePickerDialog(requireContext(), datePicker, myCalendar.get(Calendar.YEAR),  myCalendar.get(Calendar.MONTH),  myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        time.setStartIconOnClickListener{
            TimePickerDialog(requireContext(), timerPicker,hour, minute, true ).show()
        }

        vm.title_vm.observe(this.viewLifecycleOwner){
            title.setText(it)
        }

        vm.description_vm.observe(this.viewLifecycleOwner){
            description.setText(it)
        }
        vm.date_vm.observe(this.viewLifecycleOwner){
            date.editText?.setText(it)
        }
        vm.time_vm.observe(this.viewLifecycleOwner){
            time.editText?.setText(it)
        }
        vm.duration_vm.observe(this.viewLifecycleOwner){
            duration.setText(it)
        }

        vm.location_vm.observe(this.viewLifecycleOwner){
            location.setText(it)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                vm.m_title(title.text.toString())
                vm.m_description(description.text.toString())
                vm.m_date(date.editText?.text.toString())
                vm.m_time(time.editText?.text.toString())
                vm.m_duration(duration.text.toString())
                vm.m_location(location.text.toString())
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })
    }

    private fun updateLable(myCalendar: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
        date.editText?.setText(sdf.format(myCalendar.time))

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("title", title.text.toString())
        outState.putString("description", description.text.toString())
        outState.putString("date", date.editText?.text.toString())
        outState.putString("time", time.editText?.text.toString())
        outState.putString("duration", duration.text.toString())
        outState.putString("location", location.text.toString())
    }
}