package it.polito.madg34.timebanking

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

class EditProfileFragment : Fragment(R.layout.editprofilefragment_layout) {
    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    private var h = 0
    private var w = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constantScreenLayoutOnScrolling(view)



        val buttonAddSkill = view.findViewById<TextView>(R.id.textSkills)

        buttonAddSkill.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_addSkillFragment)

        }

    }





    private fun constantScreenLayoutOnScrolling(view: View) {
        val sv = view.findViewById<ScrollView>(R.id.scrollViewShow)
        val constraintL = view.findViewById<ConstraintLayout>(R.id.landLayout)
        val iv = view.findViewById<ImageView>(R.id.userImage)

        sv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    h = sv.height
                    w = sv.width
                    iv.post { iv.layoutParams = FrameLayout.LayoutParams(w, h / 3) }
                } else {
                    h = constraintL.height
                    w = constraintL.width
                    iv.post { iv.layoutParams = FrameLayout.LayoutParams(w / 3, h - 180) }
                }
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

}