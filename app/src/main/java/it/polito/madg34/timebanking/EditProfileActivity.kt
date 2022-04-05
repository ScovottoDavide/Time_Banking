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
import android.provider.MediaStore.Images
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import java.io.*


class EditProfileActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    var _fullNameMOD: String? = null
    var _nicknameMOD: String? = null
    var _emailMOD: String? = null
    var _locationMOD: String? = null

    private lateinit var takePicture: ActivityResultLauncher<Intent>
    private lateinit var takePictureGallery: ActivityResultLauncher<String>
    private lateinit var userImage: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var uri: Uri

    private var h = 0
    private var w = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val intent = intent
        var _fullName = intent.getStringExtra("fullName")
        var _nickname = intent.getStringExtra("nickname")
        var _email = intent.getStringExtra("email")
        var _location = intent.getStringExtra("location")
        var _pictureString = intent.getStringExtra("picture")
        var _skills = intent.getSerializableExtra("skills") as MutableMap<String, String>

        uri = Uri.parse(_pictureString)

        /** _skills is being received in reverse order, so transform it to be as before **/
        var reverse: MutableMap<String, String> = mutableMapOf()
        for (i in _skills.entries.reversed()) {
            reverse[i.key] = i.value
        }
        reverse.forEach {
            setSkills(it.key, it.value)
        }

        constantScreenLayoutOnScrolling()

        var fullName = findViewById<EditText>(R.id.editTextTextPersonName)
        var nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        var email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        var location = findViewById<EditText>(R.id.editTextLocation)
        userImage = findViewById(R.id.userImage)

        fullName.hint = _fullName
        nickname.hint = _nickname
        email.hint = _email
        location.hint = _location
        userImage.setImageURI(uri)

        takePicture =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(result.data)

                }
            }

        takePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri = it
            /** SAVE THE IMAGE IN THE INTERNAL STORAGE **/
            val bitmap = Images.Media.getBitmap(this.contentResolver, uri)
            val wrapper = ContextWrapper(applicationContext)
            var file = wrapper.getDir("Images", MODE_PRIVATE) // NEED ROOT ACCESS TO SEE IT ON THE PHONE
            println("FILE: "+ file.absolutePath)
            file = File(file, "GalleryPhoto"+".jpg")
            try{
                var stream : OutputStream? = null
                stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            }catch (e : IOException){
                e.printStackTrace()
            }
            // Parse the gallery image url to uri
            uri = Uri.parse(file.absolutePath)
            userImage.setImageURI(uri)
        }
    }

    private fun handleCameraImage(intent: Intent?) {
        bitmap = intent?.extras?.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            bytes
        ) // Used for compression rate of the Image : 100 means no compression

        val path: String =
            Images.Media.insertImage(this.getContentResolver(), bitmap, "xyz", null)
        uri = Uri.parse(path)
        userImage.setImageURI(uri)
    }

    private fun constantScreenLayoutOnScrolling() {
        val sv = findViewById<ScrollView>(R.id.scrollViewShow)
        val constraintL = findViewById<ConstraintLayout>(R.id.landLayout)
        val iv = findViewById<ImageView>(R.id.userImage)

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
                    iv.post { iv.layoutParams = FrameLayout.LayoutParams(w / 3, h) }
                }
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setSkills(skill: String, description: String) {
        val linearLayout = findViewById<LinearLayout>(R.id.lastLinear)

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
            this,
            com.google.android.material.R.style.TextAppearance_AppCompat_Body2
        )

        /** Prepare the arrow to be placed along with the text in the Expansion Header**/
        arrow.setImageResource(com.github.florent37.expansionpanel.R.drawable.ic_expansion_header_indicator_grey_24dp)
        /** Margin to place the arrows **/
        var wid =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 2600 else 1850
        val arrowLayoutParams = LinearLayout.LayoutParams(
            wid,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        arrowLayoutParams.gravity = Gravity.CENTER_VERTICAL or Gravity.END

        /** Add the Text and the Arrow to build the header of each skill **/
        expH.addView(arrow, arrowLayoutParams)
        expH.addView(tv)

        /** Prepare the layout for the description **/
        val layout = ExpansionLayout(this)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 10
        }

        /** Text View for the description **/
        val expansionText = TextView(this)
        expansionText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginStart = 5 }
        expansionText.id = layout.id
        expansionText.setTextAppearance(
            this,
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

    override fun onBackPressed() {

        var fullName = findViewById<EditText>(R.id.editTextTextPersonName)
        var nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        var email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        var location = findViewById<EditText>(R.id.editTextLocation)

        val returnIntent = intent

        _fullNameMOD = fullName.text.toString()
        _nicknameMOD = nickname.text.toString()
        _emailMOD = email.text.toString()
        _locationMOD = location.text.toString()
        var _picture = uri.toString()

        if (_fullNameMOD != null) returnIntent.putExtra("fullName", _fullNameMOD)
        if (_nicknameMOD != null) returnIntent.putExtra("nickname", _nicknameMOD)
        if (_emailMOD != null) returnIntent.putExtra("email", _emailMOD)
        if (_locationMOD != null) returnIntent.putExtra("location", _locationMOD)
        if (_picture != null) returnIntent.putExtra("picture", _picture)

        setResult(Activity.RESULT_OK, returnIntent)
        finish()

        super.onBackPressed()
    }

    fun showPopup(v: View) {
        val popup = PopupMenu(this, v)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.popup_menu)
        popup.show()
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

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            return when (item.itemId) {
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
            return false
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("uri", uri.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val s = savedInstanceState.getString("uri")
        uri = Uri.parse(s)
        userImage.setImageURI(uri)
    }
}