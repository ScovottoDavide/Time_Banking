package it.polito.madg34.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import java.io.*

class EditProfileFragment : Fragment() {
    val vm by navGraphViewModels<ProfileViewModel>(R.id.main)

    private var h = 0
    private var w = 0
    private lateinit var bitmap: Bitmap
    private  var uri: Uri? = null



    private lateinit var takePicture: ActivityResultLauncher<Intent>
    private lateinit var takePictureGallery: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.editprofilefragment_layout, container, false)

        if (vm.profile.value == null) {
            vm.saveServices(emptyProfile())
        }

        val buttonPopup = view.findViewById<ImageButton>(R.id.plus)
        buttonPopup.setOnClickListener(View.OnClickListener() {
            val popupMenu = PopupMenu(context, buttonPopup)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                if (item != null) {
                    when (item.itemId) {
                        R.id.select -> {
                            dispatchTakeGalleryPictureIntent()
                            true
                        }
                        R.id.camera -> {
                            dispatchTakePictureIntent()
                            true
                        }
                        else -> super.onOptionsItemSelected(item)

                    }
                } else {
                    false
                }

            })


            popupMenu.show()
        })

        return view
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val bundle : Bundle? = arguments
        val item: ProfileUser? = vm.profile.value

        val fullName = view.findViewById<EditText>(R.id.editTextTextPersonName)
        val nickname = view.findViewById<EditText>(R.id.editTextTextPersonName3)
        val email = view.findViewById<EditText>(R.id.editTextTextEmailAddress)
        val location = view.findViewById<EditText>(R.id.editTextLocation)
        val userDesc = view.findViewById<EditText>(R.id.userDesc)
        val userImage = view.findViewById<ImageView>(R.id.userImage)

        fullName.setText(item?.fullName)
        nickname.setText(item?.nickname)
        email.setText(item?.email)
        location.setText(item?.location)
        userDesc.setText(item?.aboutUser)
        userImage.setImageURI(Uri.parse(item?.img))
        var indexName = 100
        var indexDesc = -100
        item?.skills?.toSortedMap()?.forEach {
            setSkills(it.key, it.value, indexName++, indexDesc--, view)
        }


        takePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) uri = it
            else return@registerForActivityResult
            /** SAVE THE IMAGE IN THE INTERNAL STORAGE **/
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
            val wrapper = ContextWrapper(activity?.applicationContext)
            var file = wrapper.getDir("Images", AppCompatActivity.MODE_PRIVATE) // NEED ROOT ACCESS TO SEE IT ON THE PHONE

            file = File(file, "GalleryPhoto"+".jpg")

            try{
                val stream: OutputStream?
                stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                val path: String = file.absolutePath
                uri = Uri.parse(file.absolutePath)
                userImage.setImageURI(null);
                userImage.setImageURI(uri)


            }catch (e : IOException){
                e.printStackTrace()
            }
        }


        takePicture =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                bitmap = result?.data?.extras?.get("data") as Bitmap
                val bytes = ByteArrayOutputStream()
                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    bytes
                ) // Used for compression rate of the Image : 100 means no compression

                val path: String =
                    MediaStore.Images.Media.insertImage(activity?.contentResolver, bitmap, "xyz", null)
                uri = Uri.parse(path)
                userImage.setImageURI(uri)

            }

        }


        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    vm.profile.value?.apply {
                        this.fullName = fullName.text.toString()
                        this.nickname = nickname.text.toString()
                        this.email = email.text.toString()
                        this.location = location.text.toString()
                        this.aboutUser = userDesc.text.toString()
                        if(uri!=null) this.img = uri?.toString()
                    }
                    vm.saveServices(vm.profile.value!!)
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })

        constantScreenLayoutOnScrolling(view)
        val buttonAddSkill = view.findViewById<TextView>(R.id.textSkills)
        buttonAddSkill.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_addSkillFragment)

        }

    }

    private fun setSkills(
        skill: String,
        description: String,
        indexName: Int,
        indexDesc: Int,
        view: View
    ) {
        val linearLayout = view.findViewById<LinearLayout>(R.id.lastLinear)

        val expH = ExpansionHeader(linearLayout.context)
        val arrow = ImageView(linearLayout.context)
        val tv = TextView(linearLayout.context)
        val pencil = ImageButton(linearLayout.context)

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
            marginStart = 80
            topMargin = 20
        }
        tv.id = indexName
        tv.text = skill
        tv.textSize = 20F
        tv.setTextAppearance(
            activity,
            com.google.android.material.R.style.TextAppearance_AppCompat_Medium
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


        /** Allow the user to modify the skill **/
        pencil.setImageResource(R.drawable.outline_edit_24)
        pencil.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 5
        }
        pencil.setBackgroundColor(getResources().getColor(R.color.white))

        /** Add the Text and the Arrow to build the header of each skill **/
        expH.addView(arrow, arrowLayoutParams)
        expH.addView(tv)
        expH.addView(pencil)

        /** Prepare the layout for the description **/
        val layout = ExpansionLayout(activity)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 20
        }

        /** Text View for the description **/
        val expansionText = TextView(activity)
        expansionText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginStart = 5 }
        expansionText.id = indexDesc
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

        pencil.setOnClickListener {
            val bundle = bundleOf(
                "skillDescIndex" to indexDesc,
                "skillIndex" to indexName,
                "skillOld" to skill,
                "skillName" to skill,
                "skillDescription" to description
            )

            findNavController().navigate(
                R.id.action_editProfileFragment_to_editSkillFragment,
                bundle
            )
        }

    }

    private fun dispatchTakePictureIntent() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePicture.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.printStackTrace()
        }
    }

    private fun dispatchTakeGalleryPictureIntent() {
        try {
            takePictureGallery.launch("image/*")
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
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


