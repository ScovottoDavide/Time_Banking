package it.polito.madg34.timebanking

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels

class ShowProfileFragment: Fragment(R.layout.showprofilefragment_layout) {
    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    private var h = 0
    private var w = 0

    /*lateinit var fullNameView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var myLocationView: TextView
    lateinit var userDesc : TextView
    lateinit var img_view: ImageView*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.showprofilefragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       /* //val bundle : Bundle? = arguments
        val item : ProfileUser? = vm.profile.value

        fullNameView = view.findViewById(R.id.fullName)
        nicknameView = view.findViewById(R.id.nickName)
        emailView = view.findViewById(R.id.email)
        myLocationView = view.findViewById(R.id.location)
        userDesc = view.findViewById(R.id.userDesc)
        img_view = view.findViewById(R.id.imageUsr)

        vm.profile.observe(this.viewLifecycleOwner){
            fullNameView.text = item?.fullName
            nicknameView.text = item?.nickname
            emailView.text = item?.email
            myLocationView.text = item?.location
            userDesc.text = item?.aboutUser
            //img_view.setImageURI(item?.img)
        }*/




        constantScreenLayoutOnScrolling(view)



    }

    private fun constantScreenLayoutOnScrolling(view: View) {
        val sv = view.findViewById<ScrollView>(R.id.scrollViewShow)
        val constraintL = view.findViewById<ConstraintLayout>(R.id.landLayout)
        val iv = view.findViewById<ImageView>(R.id.imageUsr)

        sv.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if(resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
                    h = sv.height
                    w = sv.width
                    iv.post { iv.layoutParams = LinearLayout.LayoutParams(w, h / 3) }
                } else {
                    h = constraintL.height
                    w = constraintL.width
                    iv.post { iv.layoutParams = LinearLayout.LayoutParams(w / 3, h + 150) }
                }
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                //val bundle = arguments
                findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}