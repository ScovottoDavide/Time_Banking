package it.polito.madg34.timebanking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels

class AddSkillFragment: Fragment(R.layout.addskillfragment_layout) {
    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}