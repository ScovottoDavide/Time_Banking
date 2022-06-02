package it.polito.madg34.timebanking.TimeSlots

import android.app.AlertDialog
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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import java.text.SimpleDateFormat
import java.util.*


class TimeSlotEditFragment : Fragment() {

    val vm: TimeSlotViewModel by activityViewModels()
    val vmProfile: ProfileViewModel by activityViewModels()


    private var h: Int = 0
    private var w: Int = 0

    private lateinit var title: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var duration: TextInputEditText
    private lateinit var location: TextInputEditText
    private lateinit var date: TextInputEditText
    private lateinit var time: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var menuSkills: TextInputLayout
    private lateinit var item: TimeSlot
    private lateinit var pageTitle: TextView

    private var hour = 0;
    private var minute = 0;

    val skills = mutableListOf<String>()

    lateinit var noSkillPopup : AlertDialog
    var isPopupOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timesloteditfragment_layout, container, false)
        setHasOptionsMenu(true)

        isPopupOpen = savedInstanceState?.getBoolean("showPopup") == true
        if(isPopupOpen)
            noSkillPopup()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)
        title = view.findViewById(R.id.outlinedTitleFixed)
        description = view.findViewById(R.id.outlinedDescriptionFixed)
        duration = view.findViewById(R.id.outlinedDurationFixed)
        location = view.findViewById(R.id.outlinedLocationFixed)
        date = view.findViewById(R.id.outlinedDateFixed)
        time = view.findViewById(R.id.outlinedTimeFixed)
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

        date.setOnClickListener {
            datePicker.show(this.parentFragmentManager, "")
            datePicker.addOnPositiveButtonClickListener {
                val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                utc.timeInMillis = it
                val format = SimpleDateFormat("dd-MM-yyyy", Locale.ITALY)
                val formatted: String = format.format(utc.time)
                Log.d("DATE", formatted)
                date.setText(formatted)
            }
        }

        time.setOnClickListener {
            timePicker.show(this.parentFragmentManager, "")
            timePicker.addOnPositiveButtonClickListener {
                time.setText(
                    String.format(
                        "%02d:%02d",
                        timePicker.hour,
                        timePicker.minute
                    )
                )
            }
        }

        duration.setOnClickListener {
            durationPicker.show(this.parentFragmentManager, "")
            durationPicker.addOnPositiveButtonClickListener {
                duration.setText(
                    String.format(
                        "%dh:%dm",
                        durationPicker.hour,
                        durationPicker.minute
                    )
                )
            }
        }

        title.setText(item.title)
        description.setText(item.description)
        date.setText(item.date)
        time.setText(item.time)
        duration.setText(item.duration)
        location.setText(item.location)
        email.setText(FirestoreRepository.currentUser.email)

        vmProfile.profile.value?.skills?.forEach {
            skills.add(it.key)
        }
        val adapter = ArrayAdapter(requireContext(), R.layout.list_skills, skills)
        (menuSkills.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        if (item.related_skill.isNotEmpty()) {
            menuSkills.editText?.setText(item.related_skill)
            menuSkills.editText?.isEnabled = false
            menuSkills.isEndIconVisible = false
        } else {
            menuSkills.editText?.hint = getString(R.string.menu_skills)
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (vmProfile.profile.value?.skills?.isEmpty() == true) {
                        noSkillPopup()
                    } else {
                        if (title.text.toString().isEmpty() || description.text.toString()
                                .isEmpty() || date.text.toString().isEmpty()
                            || time.text.toString()
                                .isEmpty() || duration.text.toString()
                                .isEmpty() || location.text.toString().isEmpty()
                            || menuSkills.editText?.text.isNullOrEmpty()
                        )
                            formCheck()
                        else if(duration.text.toString() == "0h:0m"){
                            val outerDuration : TextInputLayout = view.findViewById(R.id.outlinedDuration)!!
                            outerDuration.error =  "Duration cannot be zero!"
                        }
                        else {
                            if (index >= 0 && index <= vm.currentUserAdvs.value?.size!!) {
                                vm.currentUserAdvs.value?.get(index).also {
                                    it?.title = title.text.toString()
                                    it?.description = description.text.toString()
                                    it?.date = date.text.toString()
                                    it?.time = time.text.toString()
                                    it?.duration = duration.text.toString()
                                    it?.location = location.text.toString()
                                    it?.related_skill = menuSkills.editText?.text.toString()
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
                                item.date = date.text.toString()
                                item.time = time.text.toString()
                                item.duration = duration.text.toString()
                                item.location = location.text.toString()
                                item.published_by = email.text.toString()
                                item.related_skill = menuSkills.editText?.text.toString()
                                item.refused = ""
                                item.accepted = ""
                                item.available = 1
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
                                item.date = date.text.toString()
                                item.time = time.text.toString()
                                item.duration = duration.text.toString()
                                item.location = location.text.toString()
                                item.published_by = email.text.toString()
                                item.related_skill = menuSkills.editText?.text.toString()
                                item.index = vm.currentUserAdvs.value?.size!!
                                item.refused = ""
                                item.accepted = ""
                                item.available = 1
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
                    noSkillPopup()
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

    private fun formCheck(){
        val outerTitle : TextInputLayout = view?.findViewById(R.id.outlinedTitle)!!
        val outerDesc : TextInputLayout = view?.findViewById(R.id.outlinedDescription)!!
        val outerDate : TextInputLayout = view?.findViewById(R.id.outlinedDate)!!
        val outerTime : TextInputLayout = view?.findViewById(R.id.outlinedTime)!!
        val outerDuration : TextInputLayout = view?.findViewById(R.id.outlinedDuration)!!
        val outerLocation : TextInputLayout = view?.findViewById(R.id.outlinedLocation)!!

        if(title.text.toString().isEmpty()){
            outerTitle.error =  "Please provide a title"
        }else outerTitle.error = null
        if(description.text.toString().isEmpty()){
            outerDesc.error =  "Please provide a description"
        }else outerDesc.error = null
        if(date.text.toString().isEmpty()){
            outerDate.error =  "Please choose a date"
        }else outerDate.error = null
        if(time.text.toString().isEmpty()){
            outerTime.error =  "Provide time"
        }else outerTime.error = null
        if(duration.text.toString().isEmpty()){
            outerDuration.error =  "Please choose the duration"
        }else outerDuration.error = null
        if(location.text.toString().isEmpty()){
            outerLocation.error =  "Please provide a location"
        }else outerLocation.error = null
        if(menuSkills.editText?.text.isNullOrEmpty()){
            menuSkills.error = "Please choose a skill"
        }else menuSkills.error = null
    }

    override fun onPause() {
        super.onPause()
        if(isPopupOpen)
            noSkillPopup()
    }

    fun noSkillPopup() {
        isPopupOpen = true
        noSkillPopup = AlertDialog.Builder(requireContext())
            .setTitle("Indications")
            .setMessage(
                "In order to publish an advertisement, at least one skill " +
                        "has to be set in your profile. Go back in your profile and add one."
            )
            .setPositiveButton("OK") { _, _ ->
                isPopupOpen = false
            }
            .show()

        noSkillPopup.setOnDismissListener { isPopupOpen = false }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("showPopup", isPopupOpen)
    }
}