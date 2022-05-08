package it.polito.madg34.timebanking

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.google.android.material.snackbar.Snackbar

class EditSkillFragment : Fragment() {

    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    var _index: Int = 0
    var _indexDesc: Int = 0
    var _skillDescription: String? = ""
    var _skillName: String? = ""
    var _skillOld: String? = ""

    lateinit var tv : TextView
    lateinit var editDesc : EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.editskillfragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    if(tv.text.toString().isNotEmpty()){
                       if(tv.text.toString() != _skillName){
                           vm.profile.value?.skills?.remove(_skillName)
                           if(editDesc.text.toString() != "")
                               vm.profile.value?.skills?.set(tv.text.toString(), editDesc.text.toString())
                           else
                               vm.profile.value?.skills?.set(tv.text.toString(), "No Description")
                       } else {
                           if(editDesc.text.toString() != "")
                               vm.profile.value?.skills?.replace(_skillName!!, _skillDescription!!, editDesc.text.toString())
                           else
                               vm.profile.value?.skills?.replace(_skillName!!, _skillDescription!!, "No Description")
                       }
                        vm.saveProfile(vm.profile.value!!)
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                        if(vm.profile.value?.skills?.get(tv.text.toString()) == null)
                            Snackbar.make(view, "Skill successfully removed!", Snackbar.LENGTH_LONG).show()
                        else
                            Snackbar.make(view, "Skill successfully edited!", Snackbar.LENGTH_LONG).show()
                    }
                    else {
                        Snackbar.make(view, "Skill name cannot be empty!", Snackbar.LENGTH_LONG).show()
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
                requireActivity().onBackPressed()
                true
            }
            R.id.cancel -> {
                vm.profile.value?.skills?.remove(tv.text.toString())
                vm.saveProfile(vm.profile.value!!)
                Snackbar.make(requireView(), "Skill successfully removed!", Snackbar.LENGTH_LONG).show()
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeKeyboard() {
        // this will give us the view which is currently focus in this layout
        val v: View? = this.view?.findFocus()

        val manager: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(v?.windowToken, 0)
    }
}
