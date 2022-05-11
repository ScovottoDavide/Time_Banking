package it.polito.madg34.timebanking

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class ShowProfileFragment: Fragment(R.layout.showprofilefragment_layout) {
    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    private var h = 0
    private var w = 0

    lateinit var fullNameView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var myLocationView: TextView
    lateinit var userDesc : TextView
    lateinit var img_view: ImageView

    //private val prova : ProfileUser = ProfileUser(null, "Ciao", "prova", "dddd", "dddd","CCC", mutableMapOf("Ciao" to "Ciao"))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.showprofilefragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vm._profile.value = prova
        val item : ProfileUser? = vm.profile.value

        fullNameView = view.findViewById(R.id.fullName)
        nicknameView = view.findViewById(R.id.nickName)
        emailView = view.findViewById(R.id.email)
        myLocationView = view.findViewById(R.id.location)
        userDesc = view.findViewById(R.id.userDesc)
        img_view = view.findViewById(R.id.userImg)

        vm.profile.observe(this.viewLifecycleOwner){
            fullNameView.text = item?.fullName
            nicknameView.text = item?.nickname
            emailView.text = item?.email
            myLocationView.text = item?.location
            userDesc.text = item?.aboutUser

            val navView  = activity?.findViewById<NavigationView>(R.id.nav_view)
            val header = navView?.getHeaderView(0)
            val name = header?.findViewById<TextView>(R.id.nomecognome)
            Log.d("sec", item?.fullName.toString())
            if(!item?.fullName?.isEmpty()!!)
                name?.text = item.fullName
            val email = header?.findViewById<TextView>(R.id.headerMail)
            if(!item.email?.isEmpty()!!)
                email?.text = item.email
            val imgProfile = header?.findViewById<CircleImageView>(R.id.nav_header_userImg)
            if(item?.img != null) {
                img_view.setImageURI(Uri.parse(item.img))
                imgProfile?.setImageDrawable(img_view.drawable)
            }

            item?.skills?.forEach {
                setSkills(it.key, it.value, view)
            }
        }

        constantScreenLayoutOnScrolling(view)
    }

    private fun setSkills(skill: String, description: String, view: View) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.lastLinear)

        val expH = ExpansionHeader(linearLayout.context)
        val arrow = ImageView(linearLayout.context)
        val tv = TextView(linearLayout.context)

        /** 1. Set expansion header layout params **/
        val expHLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        expH.layoutParams = expHLayoutParams
        expH.isToggleOnClick = true

        /** Prepate the textView to be inserted in the Expansion Header **/
        tv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 12
            topMargin = 8
        }
        tv.text = skill
        tv.textSize = 20F
        tv.setTextAppearance(activity, com.google.android.material.R.style.TextAppearance_AppCompat_Body2)

        /** Prepare the arrow to be placed along with the text in the Expansion Header**/
        arrow.setImageResource(com.github.florent37.expansionpanel.R.drawable.ic_expansion_header_indicator_grey_24dp)
        /** Margin to place the arrows **/
        val wid = if(resources.configuration.orientation==Configuration.ORIENTATION_LANDSCAPE) 2600 else 1850
        val arrowLayoutParams = LinearLayout.LayoutParams(
            wid,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        arrowLayoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END

        /** Add the Text and the Arrow to build the header of each skill **/
        expH.addView(arrow, arrowLayoutParams)
        expH.addView(tv)
        //expH.addView(arrow)

        /** Prepare the layout for the description **/
        val layout = ExpansionLayout(activity)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 10
        }

        /** Text View for the description **/
        val expansionText = TextView(activity)
        expansionText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginStart = 5 }
        expansionText.id = layout.id
        expansionText.setTextAppearance(activity, com.google.android.material.R.style.TextAppearance_AppCompat_Body1)
        expansionText.textSize = 15F
        expansionText.text = description

        /** Add the Text to the expandable layout **/
        layout.addView(expansionText)

        /** Add all to the parent, which is the last layout **/
        linearLayout.addView(expH)
        linearLayout.addView(layout)

        /** To let the description appear and the arrow rotate **/
        arrow.setOnClickListener {
            layout.toggle(true)
            if (layout.isExpanded)
                arrow.rotation = 90F
            else arrow.rotation = 0F
        }

    }

    private fun constantScreenLayoutOnScrolling(view: View) {
        val sv = view.findViewById<ScrollView>(R.id.scrollViewShow)
        val constraintL = view.findViewById<ConstraintLayout>(R.id.landLayout)
        val iv = view.findViewById<ImageView>(R.id.userImg)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.pencil_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
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