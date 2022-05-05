package it.polito.madg34.timebanking

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

class EditSkillFragment : Fragment(R.layout.editskillfragment_layout) {

    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    var _index : Int = 0
    var _indexDesc : Int = 0
    var _skillDescription : String ? = ""
    var _skillName : String ? = ""
    var _skillOld : String ? = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item : ProfileUser? = vm.profile.value

        val skillName = view.findViewById<EditText>(R.id.skillName)
        val skillDescription = view.findViewById<EditText>(R.id.skillDescription)

        _skillName = arguments?.getString("skillName")
        _skillDescription = arguments?.getString("skillDescription")
        _index = arguments?.getInt("skillIndex", -1)!!
        _indexDesc = arguments?.getInt("skillDescIndex", -1)!!
        _skillOld = arguments?.getString("skillOld")

        val tv = view.findViewById<TextView>(R.id.skillName)
        val editDesc = view.findViewById<EditText>(R.id.skillDescription)

        tv.text = _skillName
        editDesc.setText(_skillDescription)

        val deleteButton = view.findViewById<Button>(R.id.deleteSkill)
        deleteButton.setOnClickListener {

            //TROVARE IL MODO DI CANCELLARE
        }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //PASSARE ARGOMENTI

                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })




    }
}