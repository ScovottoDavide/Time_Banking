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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

class TimeSlotEditFragment : Fragment(R.layout.timesloteditfragment_layout) {

    val vm by navGraphViewModels<TimeSlotViewModel>(R.id.main)

    private var h: Int = 0
    private var w: Int = 0

    private lateinit var title: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var duration: TextInputEditText
    private lateinit var location: TextInputEditText
    private lateinit var date: TextInputLayout
    private lateinit var time: TextInputLayout
    private lateinit var item :TimeSlot

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


        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build())
            .build()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select Service time")
            .build()


        date.setStartIconOnClickListener {
            datePicker.show(this.parentFragmentManager, "")
            datePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("dd-MM-yyyy", Locale.ITALY)
                val formatted: String = format.format(utc.time)
                date.editText?.setText(formatted)
            }
        }

        time.setStartIconOnClickListener {
            timePicker.show(this.parentFragmentManager, "")
            timePicker.addOnPositiveButtonClickListener {
                var minutes = ""
                if (timePicker.minute == 0){
                    minutes = timePicker.minute.toString() + "0"
                }
                else minutes = timePicker.minute.toString()
                val timeString = timePicker.hour.toString() + ":" + minutes
                time.editText?.setText(timeString)
            }
        }

        val bundle = arguments
        val index = bundle?.getInt("index") ?: item.index
        if(index == -1)
            item = emptyTimeSlot()
        else {
            if(index>=0 && index < vm.listServices.value?.size!!){
                item = vm.listServices.value?.get(index)!!
            }
            else if (index > vm.listServices.value?.size!!){
                item = emptyTimeSlot()
            }
        }

        title.setText(item.title)
        description.setText(item.description)
        date.editText?.setText(item.date)
        time.editText?.setText(item.time)
        duration.setText(item.duration)
        location.setText(item.location)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(title.text.toString().isEmpty() || description.text.toString().isEmpty() || date.editText?.text.toString().isEmpty()
                        || time.editText?.text.toString().isEmpty() || duration.text.toString().isEmpty() || location.text.toString().isEmpty())
                        Toast.makeText(context, "Please, fill the entire form.", Toast.LENGTH_SHORT).show()
                    else {
                        if (index>=0 && index <= vm.listServices.value?.size!!) {
                            vm.listServices.value?.get(index).also {
                                it?.title = title.text.toString()
                                it?.description = description.text.toString()
                                it?.date = date.editText?.text.toString()
                                it?.time = time.editText?.text.toString()
                                it?.duration = duration.text.toString()
                                it?.location = location.text.toString()
                                it?.index = index
                                vm.saveServices(vm.listServices.value!!)
                                Toast.makeText(context, "Service successfully edited.", Toast.LENGTH_SHORT).show()
                            }
                        } else if(index==-1){
                            item.title = title.text.toString()
                            item.description = description.text.toString()
                            item.date = date.editText?.text.toString()
                            item.time = time.editText?.text.toString()
                            item.duration = duration.text.toString()
                            item.location = location.text.toString()
                            item.index = index
                            vm.saveServices(mutableListOf(item))
                            Toast.makeText(context, "Service successfully added.", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            item.title = title.text.toString()
                            item.description = description.text.toString()
                            item.date = date.editText?.text.toString()
                            item.time = time.editText?.text.toString()
                            item.duration = duration.text.toString()
                            item.location = location.text.toString()
                            item.index = index
                            vm.listServices.value?.add(item)
                            vm.saveServices(vm.listServices.value!!)
                            Toast.makeText(context, "Service successfully added.", Toast.LENGTH_SHORT).show()
                        }
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    }
                }
            })
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