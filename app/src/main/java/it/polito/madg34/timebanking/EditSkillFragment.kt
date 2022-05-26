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

    private var _index: Int = 0
    private var _indexDesc: Int = 0
    private var _skillDescription: String? = ""
    private var _skillName: String? = ""
    private var _skillOld: String? = ""
    private var fromCancel = false
    private var fromSave = false
    private var isPopupOpen = false
    private var isPopupDeleteOpen = false

    lateinit var tv: TextView
    lateinit var editDesc: EditText
    lateinit var profile: ProfileUser

    lateinit var dialog : AlertDialog
    lateinit var dialogDelete : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.editskillfragment_layout, container, false)
        setHasOptionsMenu(true)
        isPopupOpen = savedInstanceState?.getBoolean("showDialog") == true
        isPopupDeleteOpen = savedInstanceState?.getBoolean("showDialogDelete") == true
        fromSave = savedInstanceState?.getBoolean("fromSave") == true
        if(isPopupOpen)
            warningPopup()
        if(isPopupDeleteOpen && !vm.needRegistration)
            deletePopup()
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
                        if(!vm.needRegistration){
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
                fromSave = true
                warningPopup()
                true
            }
            R.id.cancel -> {
                if(!vm.needRegistration){
                    deletePopup()
                }else {
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
        if(!vm.needRegistration){
            isPopupOpen = true
            dialog = AlertDialog.Builder(requireContext())
                .setTitle("Warning")
                .setMessage(
                    "If you have modified the skill name all the advertisements" +
                            "related to this skill we be deleted." +
                            "Do you want to continue?"
                )
                .setPositiveButton("Yes") { _, _ ->
                    Log.d("DENTRO", "ciao")
                    if (fromSave)
                        requireActivity().onBackPressed()
                }
                .setNegativeButton("No") { _, _ ->
                }
                .show()
            dialog.setOnDismissListener { isPopupOpen = false }
        }else if (fromSave)
                requireActivity().onBackPressed()
    }

    private fun deletePopup(){
        isPopupDeleteOpen = true
        dialogDelete = AlertDialog.Builder(requireContext())
            .setTitle("Warning")
            .setMessage(
                "If you delete this skill, also all the advertisement related to " +
                        "this skill will be deleted." +
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
                isPopupDeleteOpen = true
                requireActivity().onBackPressed()
            }
            .setNegativeButton("No") { _, _ ->
                isPopupDeleteOpen = true
            }
            .show()
        dialogDelete.setOnDismissListener { isPopupDeleteOpen = false }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("showDialog", isPopupOpen)
        outState.putBoolean("showDialogDelete", isPopupDeleteOpen)
        outState.putBoolean("fromSave", fromSave)
    }
}
