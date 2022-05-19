package it.polito.madg34.timebanking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*
import kotlin.math.min
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class TimeSlotEditFragment : Fragment() {

    val vm: TimeSlotViewModel by activityViewModels()
    val vmProfile: ProfileViewModel by activityViewModels()


    private var h: Int = 0
    private var w: Int = 0

    private lateinit var title: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var duration: TextInputLayout
    private lateinit var location: TextInputEditText
    private lateinit var date: TextInputLayout
    private lateinit var time: TextInputLayout
    private lateinit var email: TextInputEditText
    private lateinit var menuSkills: TextInputLayout
    private lateinit var item: TimeSlot
    private lateinit var pageTitle: TextView

    private var hour = 0;
    private var minute = 0;

    val skills = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timesloteditfragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)
        title = view.findViewById(R.id.outlinedTitleFixed)
        description = view.findViewById(R.id.outlinedDescriptionFixed)
        duration = view.findViewById(R.id.outlinedDuration)
        location = view.findViewById(R.id.outlinedLocationFixed)
        date = view.findViewById(R.id.outlinedDate)
        time = view.findViewById(R.id.outlinedTime)
        email = view.findViewById(R.id.outlinedMailFixed)
        menuSkills = view.findViewById(R.id.menuSkills)

        val bundle = arguments
        val index = bundle?.getInt("index") ?: item.index
        if (index == -1) {
            item = emptyTimeSlot()
            pageTitle = view.findViewById(R.id.titleEditService)
            pageTitle.setText(R.string.AddService)
        } else {
            if (index >= 0 && index < vm.currentUserAdvs.value?.size!!) {
                item = vm.currentUserAdvs.value?.get(index)!!
            } else if (index > vm.currentUserAdvs.value?.size!!) {
                item = emptyTimeSlot()
                pageTitle = view.findViewById(R.id.titleEditService)
                pageTitle.setText(R.string.AddService)
            }
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(
                CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
            )
            .build()

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select Service time")
            .build()

        val durationPicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select Service duration")
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
                time.editText?.setText(
                    String.format(
                        "%02d:%02d",
                        timePicker.hour,
                        timePicker.minute
                    )
                )
            }
        }

        duration.setStartIconOnClickListener {
            durationPicker.show(this.parentFragmentManager, "")
            durationPicker.addOnPositiveButtonClickListener {
                duration.editText?.setText(
                    String.format(
                        "%d hours and %d minutes",
                        durationPicker.hour,
                        durationPicker.minute
                    )
                )
            }
        }

        title.setText(item.title)
        description.setText(item.description)
        date.editText?.setText(item.date)
        time.editText?.setText(item.time)
        duration.editText?.setText(item.duration)
        location.setText(item.location)
        email.setText(FirestoreRepository.currentUser.email)

        vmProfile.profile.value?.skills?.forEach {
            skills.add(it.key)
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_skills, skills)
        (menuSkills.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        if(item.related_skill.isNotEmpty()) {
            menuSkills.editText?.hint = item.related_skill
            menuSkills.editText?.isEnabled = false
            menuSkills.isEndIconVisible = false
        }else{
            menuSkills.editText?.hint = getString(R.string.menu_skills)
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (vmProfile.profile.value?.skills?.isEmpty() == true) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Indications")
                            .setMessage(
                                "In order to publish an advertisement, at least one skill " +
                                        "has to be set in your profile. Go back in your profile and add one."
                            )
                            .setPositiveButton("OK") { _, _ ->
                            }
                            .show()
                    } else {
                        if (title.text.toString().isEmpty() || description.text.toString()
                                .isEmpty() || date.editText?.text.toString().isEmpty()
                            || time.editText?.text.toString()
                                .isEmpty() || duration.editText?.text.toString()
                                .isEmpty() || location.text.toString().isEmpty()
                            || menuSkills.editText?.text.isNullOrEmpty()
                        )
                            Toast.makeText(
                                context,
                                "Please, fill the entire form.",
                                Toast.LENGTH_SHORT
                            ).show()
                        else {
                            if (index >= 0 && index <= vm.currentUserAdvs.value?.size!!) {
                                vm.currentUserAdvs.value?.get(index).also {
                                    it?.title = title.text.toString()
                                    it?.description = description.text.toString()
                                    it?.date = date.editText?.text.toString()
                                    it?.time = time.editText?.text.toString()
                                    it?.duration = duration.editText?.text.toString()
                                    it?.location = location.text.toString()
                                    it?.related_skill = menuSkills.editText?.text.toString()
                                    //it?.index = vm.currentUserAdvs.value?.size!!
                                    vm.currentShownAdv = vm.currentUserAdvs.value?.get(index)!!
                                    vm.updateAdv(vm.currentUserAdvs.value?.get(index)!!)
                                    Snackbar.make(
                                        view,
                                        "Service successfully edited!",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            } else if (index == -1) {
                                item.title = title.text.toString()
                                item.description = description.text.toString()
                                item.date = date.editText?.text.toString()
                                item.time = time.editText?.text.toString()
                                item.duration = duration.editText?.text.toString()
                                item.location = location.text.toString()
                                item.published_by = email.text.toString()
                                item.related_skill = menuSkills.editText?.text.toString()
                                item.index = vm.currentUserAdvs.value?.size!!
                                vm.saveAdv(item)
                                Snackbar.make(
                                    view,
                                    "Service successfully added!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            } else {
                                item.title = title.text.toString()
                                item.description = description.text.toString()
                                item.date = date.editText?.text.toString()
                                item.time = time.editText?.text.toString()
                                item.duration = duration.editText?.text.toString()
                                item.location = location.text.toString()
                                item.published_by = email.text.toString()
                                item.related_skill = menuSkills.editText?.text.toString()
                                item.index = vm.currentUserAdvs.value?.size!!
                                vm.saveAdv(item)
                                Snackbar.make(
                                    view,
                                    "Service successfully added!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                            if (isEnabled) {
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_page, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                if (vmProfile.profile.value?.skills?.isEmpty() == false) {
                    closeKeyboard()
                    requireActivity().onBackPressed()
                } else {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Indications")
                        .setMessage(
                            "In order to publish an advertisement, at least one skill " +
                                    "has to be set in your profile. Go back in your profile and add one."
                        )
                        .setPositiveButton("OK") { _, _ ->
                        }
                        .show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeKeyboard() {
        // this will give us the view which is currently focus in this layout
        val v: View? = this.view?.findFocus()

        val manager: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(v?.windowToken, 0)
    }
}