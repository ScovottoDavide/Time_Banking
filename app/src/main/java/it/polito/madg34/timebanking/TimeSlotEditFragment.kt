package it.polito.madg34.timebanking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.timesloteditfragment_layout) {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private var h: Int = 0
    private var w: Int = 0

    private lateinit var _date: String;
    private lateinit var _time: String;
    private lateinit var _title: String;
    private lateinit var _description: String;
    private lateinit var _duration: String;
    private lateinit var _location: String;

    private lateinit var title: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var duration: TextInputEditText
    private lateinit var location: TextInputEditText
    private lateinit var date: TextInputLayout
    private lateinit var time: TextInputLayout

    private var hour = 0;
    private var minute = 0;

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
        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalendar)
        }

        val timerPicker = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
            time.editText?.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
        }

        date.setStartIconOnClickListener {
            DatePickerDialog(
                requireContext(),
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        time.setStartIconOnClickListener {
            TimePickerDialog(requireContext(), timerPicker, hour, minute, true).show()
        }

        val bundle = arguments
        val item: TimeSlot? = vm.listServices.value?.get(bundle?.getInt("index")!!)

        title.setText(item?.title)
        description.setText(item?.description)
        date.editText?.setText(item?.date)
        time.editText?.setText(item?.time)
        duration.setText(item?.duration.toString())
        location.setText(item?.location)


        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vm.listServices.value?.also {
                        it.forEach {
                            it.also {
                                it.title = title.text.toString()
                                it.description = description.text.toString()
                                it.date = date.editText?.text.toString()
                                it.time = time.editText?.text.toString()
                                it.duration = duration.text.toString()
                                it.location = location.text.toString()
                            }
                        }
                    }
                    vm.saveServices(vm.listServices.value!!)
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

    private fun halfWidth(view: View) {
        val row = view.findViewById<TableRow>(R.id.RowDateTime)
        val date = view.findViewById<TextInputLayout>(R.id.outlinedDate)
        val time = view.findViewById<TextInputLayout>(R.id.outlinedTime)

        row.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    h = row.height
                    w = row.width
                    date.post { date.layoutParams = TableRow.LayoutParams(w / 2, h) }
                    time.post { time.layoutParams = TableRow.LayoutParams(w / 2, h) }
                } else {
                    h = row.height
                    w = row.width
                    date.post { date.layoutParams = TableRow.LayoutParams(w / 2, h) }
                    time.post { time.layoutParams = TableRow.LayoutParams(w / 2, h) }
                }
                row.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}