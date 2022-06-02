package it.polito.madg34.timebanking.Profile

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.R

class ShowProfileFragment : Fragment(R.layout.showprofilefragment_layout) {
    val vm: ProfileViewModel by activityViewModels()

    private var h = 0
    private var w = 0

    private var hTime = 0
    private var wTime = 0

    lateinit var fullNameView: TextInputEditText
    lateinit var nicknameView: TextInputEditText
    lateinit var emailView: TextInputEditText
    lateinit var myLocationView: TextInputEditText
    lateinit var userDesc: TextInputEditText
    lateinit var img_view: CircleImageView
    lateinit var timeCredit : EditText
    private var profile: ProfileUser? = ProfileUser()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.showprofilefragment_layout, container, false)
        setHasOptionsMenu(true)
        halfWidth(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fullNameView = view.findViewById(R.id.outlinedFullnameFixed)
        nicknameView = view.findViewById(R.id.outlinedNickNameFixed)
        emailView = view.findViewById(R.id.outlinedEmailFixed)
        myLocationView = view.findViewById(R.id.outlinedLocationFixed)
        userDesc = view.findViewById(R.id.outlinedAboutmeFixed)
        img_view = view.findViewById(R.id.userImg)
        timeCredit = view.findViewById(R.id.timeCredit)


        if (vm.clickedEmail.value != FirestoreRepository.currentUser.email && (vm.clickedEmail.value?.isNotEmpty() == true)) {
            Log.d("EMAIL", vm.clickedEmail.value.toString() + " : " + FirestoreRepository.currentUser.email)
            vm.clickedEmail.observe(viewLifecycleOwner) {
                if (it == null)
                    Toast.makeText(context, "Firebase Failure!", Toast.LENGTH_LONG).show()
                else {
                    vm.loadViewProfile().addOnSuccessListener {
                        profile = vm.profileToShow
                        setProfile(view)
                    }
                }
            }
        } else {
            vm.getDBUser().observe(viewLifecycleOwner) {
                if (it == null)
                    Toast.makeText(context, "Firebase Failure!", Toast.LENGTH_LONG).show()
                else {
                    profile = it
                    setProfile(view)
                }
            }
        }

        constantScreenLayoutOnScrolling(view)
    }

    private fun setProfile(view: View) {
        fullNameView.setText(profile?.fullName)
        nicknameView.setText(profile?.nickname)
        emailView.setText(profile?.email)
        myLocationView.setText(profile?.location)
        userDesc.setText(profile?.aboutUser)
        val range = getTimeCreditRange(profile?.total_time)
        if(range == -1)
            timeCredit.setTextColor(resources.getColor(R.color.Red))
        else if (range == 0)
            timeCredit.setTextColor(resources.getColor(R.color.Orange))
        else
            timeCredit.setTextColor(resources.getColor(R.color.LimeGreen))
        timeCredit.setText("Time credit: "+profile?.total_time)

        val navView = activity?.findViewById<NavigationView>(R.id.nav_view)
        val header = navView?.getHeaderView(0)
        val name = header?.findViewById<TextView>(R.id.nomecognome)
        if (!profile?.fullName?.isEmpty()!!)
            name?.text = profile!!.fullName
        val email = header?.findViewById<TextView>(R.id.headerMail)
        if (!profile!!.email?.isEmpty()!!)
            email?.text = profile!!.email
        val imgProfile = header?.findViewById<CircleImageView>(R.id.nav_header_userImg)
        if (!profile!!.img.isNullOrEmpty()) {
            //img_view.setImageURI(Uri.parse(profile!!.img))
            Glide.with(this).load(profile!!.img).diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform().into(img_view)
            Glide.with(this).load(profile!!.img).diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform().into(imgProfile!!)
            //imgProfile?.setImageDrawable(img_view.drawable)
        } else {
            img_view.setImageDrawable(resources.getDrawable(R.drawable.user_icon))
        }

        profile?.skills?.forEach {
            setSkills(it.key, it.value, view)
        }
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
        tv.setTextAppearance(
            activity,
            com.google.android.material.R.style.TextAppearance_AppCompat_Body2
        )

        /** Prepare the arrow to be placed along with the text in the Expansion Header**/
        arrow.setImageResource(com.github.florent37.expansionpanel.R.drawable.ic_expansion_header_indicator_grey_24dp)
        /** Margin to place the arrows **/
        val wid =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2600 else 1850
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
        expansionText.setTextAppearance(
            activity,
            com.google.android.material.R.style.TextAppearance_AppCompat_Body1
        )
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
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
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
        if (vm.clickedEmail.value == FirestoreRepository.currentUser.email) {
            inflater.inflate(R.menu.pencil_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                vm.localProfile = profile
                findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun halfWidth(view: View) {
        val row = view.findViewById<TableRow>(R.id.profileRow)
        val myAccount = view.findViewById<TextView>(R.id.AccountInfo)
        val timeCredit = view.findViewById<EditText>(R.id.timeCredit)

        row.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    hTime = row.height
                    wTime = row.width
                    myAccount.post { myAccount.layoutParams = TableRow.LayoutParams(wTime / 2, hTime) }
                    timeCredit.post { timeCredit.layoutParams = TableRow.LayoutParams(wTime / 2, hTime) }
                } else {
                    hTime = row.height
                    wTime = row.width
                    myAccount.post { myAccount.layoutParams = TableRow.LayoutParams(wTime / 2, hTime) }
                    timeCredit.post { timeCredit.layoutParams = TableRow.LayoutParams(wTime / 2, hTime) }
                }
                row.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getTimeCreditRange(item : String?) : Int{
        val item1 = item?.split(":")?.toTypedArray()
        val sxItem1 = item1?.get(0)?.removeSuffix("h")
        val dxItem1 = item1?.get(1)?.removeSuffix("m")

        if(sxItem1?.toInt() == 0){
            if(dxItem1?.toInt() == 0)
                return -1
            else
                return 0
        } else if(sxItem1?.toInt()!! > 0){
            return 1
        }
        return -1
    }
}