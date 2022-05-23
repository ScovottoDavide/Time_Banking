package it.polito.madg34.timebanking

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar

class EditSkillFragment : Fragment() {

    val vm: ProfileViewModel by activityViewModels()

    var _index: Int = 0
    var _indexDesc: Int = 0
    var _skillDescription: String? = ""
    var _skillName: String? = ""
    var _skillOld: String? = ""
    var fromCancel = false
    var fromSave = false

    lateinit var tv: TextView
    lateinit var editDesc: EditText
    lateinit var profile: ProfileUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.editskillfragment_layout, container, false)
        setHasOptionsMenu(true)
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profile = vm.localProfile!!

        _skillName = arguments?.getString("skillName")
        _skillDescription = arguments?.getString("skillDescription")
        _index = arguments?.getInt("skillIndex", -1)!!
        _indexDesc = arguments?.getInt("skillDescIndex", -1)!!
        _skillOld = arguments?.getString("skillOld")

        tv = view.findViewById(R.id.skillName)
        editDesc = view.findViewById(R.id.skillDescription)

        tv.text = _skillName
        editDesc.setText(_skillDescription)

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //PASSARE ARGOMENTI
                    if (fromSave || fromCancel) {
                        if (tv.text.toString().isNotEmpty()) {
                            val t = tv.text.mapIndexed { index, c ->
                                if (index==0) {
                                    c.uppercaseChar()
                                }
                                else {
                                    c.lowercaseChar()
                                }
                            }
                            var sNew = t.joinToString("")
                            if(sNew.last() == ' ')
                                sNew = sNew.dropLast(1)
                            if (sNew != _skillName) {
                                profile.skills?.remove(_skillName)
                                if (editDesc.text.toString() != "")
                                    profile.skills?.set(
                                        sNew,
                                        editDesc.text.toString()
                                    )
                                else
                                    profile.skills?.set(sNew, "No Description")
                            } else {
                                if (editDesc.text.toString() != "")
                                    profile.skills?.replace(
                                        _skillName!!,
                                        _skillDescription!!,
                                        editDesc.text.toString()
                                    )
                                else
                                    profile.skills?.replace(
                                        _skillName!!,
                                        _skillDescription!!,
                                        "No Description"
                                    )
                            }
                            vm.modifyUserProfile(profile)
                            if (isEnabled) {
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
                            if (profile.skills?.get(sNew) == null)
                                Snackbar.make(
                                    view,
                                    "Skill successfully removed!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            else
                                Snackbar.make(
                                    view,
                                    "Skill successfully edited!",
                                    Snackbar.LENGTH_LONG
                                ).show()
                        } else {
                            Snackbar.make(view, "Skill name cannot be empty!", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Warning")
                            .setMessage("Do you want to discard the changes?")
                            .setPositiveButton("Yes") { _, _ ->
                                if (isEnabled) {
                                    isEnabled = false
                                    requireActivity().onBackPressed()
                                }
                            }
                            .setNegativeButton("No") { _, _ ->
                            }
                            .show()
                    }
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_page, menu)
        inflater.inflate(R.menu.cancel_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                closeKeyboard()
                //requireActivity().onBackPressed()
                fromSave = true
                warningPopup()
                true
            }
            R.id.cancel -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Warning")
                    .setMessage(
                        "You have modified the skill name. This will cause" +
                                "the deletion of all the advertisement related to this skill." +
                                "Do you want to continue?"
                    )
                    .setPositiveButton("Yes") { _, _ ->
                        val profile = vm.localProfile
                        profile?.skills?.remove(tv.text.toString())
                        vm.modifyUserProfile(profile!!)
                        Snackbar.make(
                            requireView(),
                            "Skill successfully removed!",
                            Snackbar.LENGTH_LONG
                        ).show()
                        fromCancel = true
                        requireActivity().onBackPressed()
                    }
                    .setNegativeButton("No") { _, _ ->
                    }
                    .show()
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

    private fun warningPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("Warning")
            .setMessage(
                "You have modified the skill name. This will cause" +
                        "the deletion of all the advertisement related to this skill." +
                        "Do you want to continue?"
            )
            .setPositiveButton("Yes") { _, _ ->
                if (fromSave)
                    requireActivity().onBackPressed()
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()
    }
}
