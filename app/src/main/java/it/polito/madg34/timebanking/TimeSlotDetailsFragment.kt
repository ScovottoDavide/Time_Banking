package it.polito.madg34.timebanking

import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.*
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TimeSlotDetailsFragment : Fragment() {

    val vm: TimeSlotViewModel by activityViewModels()
    val vmSkills: SkillsViewModel by activityViewModels()
    val vmProfile: ProfileViewModel by activityViewModels()


    private var h: Int = 0
    private var w: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.timeslotdetailsfragment_layout, container, false)
        setHasOptionsMenu(true)
        halfWidth(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        halfWidth(view)

        val item: TimeSlot? = vm.currentShownAdv

        val title = view.findViewById<TextInputEditText>(R.id.outlinedTitleFixed)
        val description = view.findViewById<TextInputEditText>(R.id.outlinedDescriptionFixed)
        val date = view.findViewById<TextInputEditText>(R.id.outlinedDateFixed)
        val time = view.findViewById<TextInputEditText>(R.id.outlinedTimeFixed)
        val duration = view.findViewById<TextInputEditText>(R.id.outlinedDurationFixed)
        val location = view.findViewById<TextInputEditText>(R.id.outlinedLocationFixed)
        val email = view.findViewById<TextInputEditText>(R.id.outlinedMailFixed)
        val skill = view.findViewById<TextInputEditText>(R.id.outlinedSkillFixed)

        // Observe in order to get automatically the updated values
        vm.getDBTimeSlots().observe(this.viewLifecycleOwner) {
            title.setText(item?.title)
            description.setText(item?.description)
            date.setText(item?.date)
            time.setText(item?.time)
            duration.setText(item?.duration)
            location.setText(item?.location)
            val text = "<a href=''> ${item?.published_by} </a>"
            email.setText(Html.fromHtml(text))
            skill.setText(item?.related_skill)
        }

        if(vmSkills.fromHome.value!!){
            email.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Message")
                    .setMessage("Do you want to visit ${item?.published_by} profile? ")
                    .setPositiveButton("Yes") { _, _ ->
                        vmProfile.clickedEmail.value = item?.published_by.toString()
                        vmSkills.fromHome.value = true
                        findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_showProfileFragment)
                    }
                    .setNegativeButton("No") { _, _ ->
                    }
                    .show()
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!vmSkills.fromHome.value!!)
            inflater.inflate(R.menu.pencil_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                val bundle = arguments
                findNavController().navigate(
                    R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment,
                    bundle
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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