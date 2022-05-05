package it.polito.madg34.timebanking

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels

class AddSkillFragment: Fragment(R.layout.addskillfragment_layout) {
    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item : ProfileUser? = vm.profile.value

        val skillName = view.findViewById<EditText>(R.id.skillName)
        val skillDesc = view.findViewById<EditText>(R.id.skillDescription)

        skillName.hint = "New skill name ..."
        skillDesc.hint = "New skill description ..."

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vm.profile.value?.also {
                        it.also {

                        }
                    }
                    //vm.saveServices(vm.profile.value!!)
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })





    }

}