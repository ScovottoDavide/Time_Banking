package it.polito.madg34.timebanking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment: Fragment(R.layout.timesloteditfragment_layout) {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private lateinit var  _date : String;
    private lateinit var  _time : String;
    private lateinit var _title : String;
    private lateinit var _description : String;
    private lateinit var _duration : String;
    private lateinit var _location : String;



    private lateinit var  date : TextView;
    private lateinit var button : ImageButton;
    private lateinit var  time : TextView;
    private lateinit var button_T : ImageButton;
    private lateinit var title : EditText;
    private lateinit var description : EditText;
    private lateinit var duration : EditText;
    private lateinit var location : EditText;




    private  var hour = 0;
    private  var minute = 0;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState != null) {
            _title = savedInstanceState?.getString("title").toString()
            _description = savedInstanceState?.getString("description").toString()
            _date = savedInstanceState?.getString("date").toString()
            _time = savedInstanceState?.getString("time").toString()
            _duration = savedInstanceState?.getString("duration").toString()
            _location = savedInstanceState?.getString("location").toString()

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

        button = view.findViewById<ImageButton>(R.id.button1)

        button_T = view.findViewById<ImageButton>(R.id.button2)



        title = view.findViewById<EditText>(R.id.title_slot_edit)
        description = view.findViewById<EditText>(R.id.description_slot_edit)
        duration = view.findViewById<EditText>(R.id.duration_slot_edit)
        location = view.findViewById<EditText>(R.id.location_slot_edit)
        date = view.findViewById<TextView>(R.id.date_slot_edit)
        time = view.findViewById<TextView>(R.id.time_slot_edit)



        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener {view , year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalendar)
        }


        val timerPicker = TimePickerDialog.OnTimeSetListener {timePicker, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
            time.setText(String.format(Locale.getDefault(),"%02d:%02d", hour , minute))
        }


        button.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker, myCalendar.get(Calendar.YEAR),  myCalendar.get(Calendar.MONTH),  myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        button_T.setOnClickListener{
            TimePickerDialog(requireContext(), timerPicker,hour, minute, true ).show()
        }

        vm.title_vm.observe(this.viewLifecycleOwner){
            title.setText("$it")
        }

        vm.description_vm.observe(this.viewLifecycleOwner){
            description.setText("$it")
        }
        vm.date_vm.observe(this.viewLifecycleOwner){
            date.setText("$it")
        }
        vm.time_vm.observe(this.viewLifecycleOwner){
            time.setText("$it")
        }
        vm.duration_vm.observe(this.viewLifecycleOwner){
            duration.setText("$it")
        }

        vm.location_vm.observe(this.viewLifecycleOwner){
            location.setText("$it")
        }









        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                vm.m_title(title.text.toString())
                vm.m_description(description.text.toString())
                vm.m_date(date.text.toString())
                vm.m_time(time.text.toString())
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
        date.setText(sdf.format(myCalendar.time))

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("title", title.text.toString())
        outState.putString("description", description.text.toString())
        outState.putString("date", date.text.toString())
        outState.putString("time", time.text.toString())
        outState.putString("duration", duration.text.toString())
        outState.putString("location", location.text.toString())

    }




}