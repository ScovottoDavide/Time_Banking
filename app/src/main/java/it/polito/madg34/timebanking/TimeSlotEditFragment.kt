package it.polito.madg34.timebanking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment: Fragment(R.layout.timesloteditfragment_layout) {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById<ImageButton>(R.id.button1)
        date = view.findViewById<TextView>(R.id.date_slot_edit)
        time = view.findViewById<TextView>(R.id.time_slot_edit)
        button_T = view.findViewById<ImageButton>(R.id.button2)



        val title = view.findViewById<TextView>(R.id.title_slot_edit)
        val description = view.findViewById<TextView>(R.id.description_slot_edit)
        val duration = view.findViewById<TextView>(R.id.duration_slot_edit)
        val location = view.findViewById<TextView>(R.id.location_slot_edit)

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






}