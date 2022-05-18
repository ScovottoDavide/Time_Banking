package it.polito.madg34.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.io.*

class EditProfileFragment : Fragment() {
    val vm: ProfileViewModel by activityViewModels()

    private var h = 0
    private var w = 0
    private lateinit var bitmap: Bitmap
    private var uri: Uri? = null
    lateinit var file : File

    private lateinit var takePicture: ActivityResultLauncher<Intent>
    private lateinit var takePictureGallery: ActivityResultLauncher<String>

    var item: ProfileUser = ProfileUser()
    var isRegistration = false

    lateinit var fullName: EditText
    lateinit var nickname: EditText
    lateinit var email: EditText
    lateinit var location: EditText
    lateinit var userDesc: EditText
    lateinit var userImage: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.editprofilefragment_layout, container, false)
        setHasOptionsMenu(true)
        // Disable the navigation icon
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        item = vm.localProfile!!

        if (item.email.isNullOrEmpty()) {
            isRegistration = true
        }

        fullName = view.findViewById(R.id.editTextTextPersonName)
        nickname = view.findViewById(R.id.editTextTextPersonName3)
        email = view.findViewById(R.id.editTextTextEmailAddress)
        location = view.findViewById(R.id.editTextLocation)
        userDesc = view.findViewById(R.id.userDesc)
        userImage = view.findViewById(R.id.userImage)

        vm.currentUrl.observe(viewLifecycleOwner){
            item.img = vm.currentUrl.value
            Log.d("PATH10", "${item.img}")
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

        if (isRegistration) {
            val title = view.findViewById<TextView>(R.id.AccountInfo)
            title.setText(getString(R.string.registration))
            if(!vm.showed){
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Registration")
                    .setMessage("Before continuing, compile the form to give us some important information about you." +
                            "If you go back, you will be logged out.")
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .show()
                vm.showed = true
            }
        }

        fullName.setText(item.fullName)
        nickname.setText(item.nickname)
        //email.setText(item.email)
        // To don't let user change his email (primary key)
        email.setText(FirestoreRepository.currentUser.email.toString())
        email.isEnabled = false
        location.setText(item.location)
        userDesc.setText(item.aboutUser)
        if (!item.img.isNullOrEmpty()) {
            //userImage.setImageURI(Uri.parse(item.img))
            Glide.with(this).load(item.img).into(userImage)
        } else userImage.setImageResource(R.drawable.user)

        var indexName = 100
        var indexDesc = -100
        item.skills?.toSortedMap()?.forEach {
            setSkills(it.key, it.value, indexName++, indexDesc--, view)
        }

        takePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) uri = it
            else return@registerForActivityResult
            /** SAVE THE IMAGE IN THE INTERNAL STORAGE **/
            bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)
            val wrapper = ContextWrapper(activity?.applicationContext)
            file = wrapper.getDir(
                "Images",
                AppCompatActivity.MODE_PRIVATE
            ) // NEED ROOT ACCESS TO SEE IT ON THE PHONE

            file = File(file, item.email + ".jpg")

            try {
                val stream: OutputStream?
                stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                uri = Uri.parse(file.absolutePath)
                userImage.setImageURI(null)
                userImage.setImageURI(uri)
                vm.currentPhotoPath = file.path
                Log.d("PATH2", "${vm.currentPhotoPath}")
                //item.img = uri.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        takePicture =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    bitmap = result?.data?.extras?.get("data") as Bitmap
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        bytes
                    ) // Used for compression rate of the Image : 100 means no compression

                    val path: String =
                        MediaStore.Images.Media.insertImage(
                            activity?.contentResolver,
                            bitmap,
                            "xyz",
                            ""
                        )
                    uri = Uri.parse(path)
                    userImage.setImageURI(uri)
                    item.img = uri.toString()
                }
            }

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*item.fullName = fullName.text.toString()
                    item.nickname = nickname.text.toString()
                    item.email = email.text.toString()
                    item.location = location.text.toString()
                    item.aboutUser = userDesc.text.toString()*/
                    /*vm.currentUrl.observe(viewLifecycleOwner) {
                        uploadImage()
                    }*/
                    uploadImage()
                    updateProfile()

                    if (!item.fullName.isNullOrEmpty() && !item.nickname.isNullOrEmpty() && !item.email.isNullOrEmpty()
                        && !item.location.isNullOrEmpty() && !item.aboutUser.isNullOrEmpty()
                    ) {

                        vm.localProfile = item
                        vm.modifyUserProfile(vm.localProfile!!)
                        if (isRegistration){
                            Snackbar.make(
                                view,
                                "Profile successfully created",
                                Snackbar.LENGTH_LONG
                            ).show()
                            vm.listenerNavigation = null
                        }

                        else
                            Snackbar.make(view, "Profile successfully edited", Snackbar.LENGTH_LONG)
                                .show()
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    } else
                        Snackbar.make(view, "Complete profile registration", Snackbar.LENGTH_SHORT)
                            .show()
                }
            })

        constantScreenLayoutOnScrolling(view)
        val buttonAddSkill = view.findViewById<TextView>(R.id.textSkills)
        buttonAddSkill.setOnClickListener {
            updateProfile()
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

            vm.localProfile = item
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_page, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                closeKeyboard()
                // Backing is too fast--> the closing of keyboard is too slow!
                Thread.sleep(100)
                if(isRegistration){
                    vm.needRegistration = false
                }
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateProfile() {
        vm.localProfile = ProfileUser(
            fullName = fullName.text.toString(),
            nickname = nickname.text.toString(),
            email = email.text.toString(),
            location = location.text.toString(),
            img = item.img,
            aboutUser = userDesc.text.toString(),
            skills = item.skills
        )
        item = vm.localProfile!!
    }

    private fun uploadImage() {
        // Create a storage reference from our app
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://time-banking-g34.appspot.com")
        var file = Uri.fromFile(File(vm.currentPhotoPath))
        Log.d("PATH", "$file")
        // Create a reference to 'images/mountains.jpg'
        val profileImageRef = storageRef.child("images/${FirestoreRepository.currentUser.email.toString()}.jpg")
        val uploadTask = profileImageRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(context, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            //Toast.makeText(context, "Successful upload profile image", Toast.LENGTH_SHORT).show()
            val result = it.metadata!!.reference!!.downloadUrl
            result.addOnSuccessListener {
                vm.currentUrl.value = it.toString()
                Log.d("PATH3", "${vm.currentUrl.value}")
            }
        }

        /*profileImageRef.downloadUrl.addOnSuccessListener {
//            Toast.makeText(context, "Successful download profile image", Toast.LENGTH_SHORT).show()
            vm.changeUrl(it.toString())
            Log.d("PATH3", "${vm.currentUrl.value}")
        }.addOnFailureListener {
            Toast.makeText(context, "Fail to download profile image", Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onPause() {
        super.onPause()
        closeKeyboard()
        updateProfile()
    }

    private fun closeKeyboard() {
        // this will give us the view which is currently focus in this layout
        val v: View? = this.view?.findFocus()

        val manager: InputMethodManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(v?.windowToken, 0)
    }
}


