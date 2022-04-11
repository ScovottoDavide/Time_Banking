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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import org.w3c.dom.Text
import java.io.*


class EditProfileActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {
    var _fullNameMOD: String? = null
    var _nicknameMOD: String? = null
    var _emailMOD: String? = null
    var _locationMOD: String? = null
    var _aboutUserMOD: String? = null

    private lateinit var takePicture: ActivityResultLauncher<Intent>
    private lateinit var takePictureGallery: ActivityResultLauncher<String>
    private lateinit var userImage: ImageView
    private lateinit var bitmap: Bitmap
    private lateinit var uri: Uri
    private var _skills : MutableMap<String, String>? = null

    private lateinit var editSkillResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var addSkillResultLauncher: ActivityResultLauncher<Intent>

    private var h = 0
    private var w = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        val ab = supportActionBar
        if (ab != null) ab.title = "Time Banking"

        val intent = intent
        val _fullName = intent.getStringExtra("fullName")
        val _nickname = intent.getStringExtra("nickname")
        val _email = intent.getStringExtra("email")
        val _location = intent.getStringExtra("location")
        val _aboutUser = intent.getStringExtra("aboutUser")
        val _pictureString = intent.getStringExtra("picture")
        _skills = intent.getSerializableExtra("skills") as MutableMap<String, String>?

        uri = Uri.parse(_pictureString)

        var indexName = 100
        var indexDesc = -100
        _skills?.toSortedMap()?.forEach {
            setSkills(it.key, it.value, indexName++, indexDesc--)
        }

        constantScreenLayoutOnScrolling()

        val fullName = findViewById<EditText>(R.id.editTextTextPersonName)
        val nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val location = findViewById<EditText>(R.id.editTextLocation)
        val userDesc = findViewById<EditText>(R.id.userDesc)
        userImage = findViewById(R.id.userImage)

        fullName.setText(_fullName)
        nickname.setText(_nickname)
        email.setText(_email)
        location.setText(_location)
        userDesc.setText(_aboutUser)
        userImage.setImageURI(uri)

        takePicture =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(result.data)

                }
            }

        takePictureGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if(it != null) uri = it
            else return@registerForActivityResult
            /** SAVE THE IMAGE IN THE INTERNAL STORAGE **/
             bitmap = Images.Media.getBitmap(this.contentResolver, uri)
            val wrapper = ContextWrapper(applicationContext)
            var file = wrapper.getDir("Images", MODE_PRIVATE) // NEED ROOT ACCESS TO SEE IT ON THE PHONE

            println("FILE: "+ file.absolutePath)
            file = File(file, "GalleryPhoto"+".jpg")
            try{
                val stream: OutputStream?
                stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                val path: String =
                    Images.Media.insertImage(this.getContentResolver(), bitmap, "xyz", null)
                // Parse the gallery image url to uri
                uri = Uri.parse(path)
                userImage.setImageURI(uri)
            }catch (e : IOException){
                e.printStackTrace()
            }
        }

        var skillOld = ""
        var skillName = ""
        var skillDescription = ""
        var skillIndex :Int? = 0
        var skillIndexDesc : Int? = 0
        /** GET SKILL MODIFICATION AFTER EDITSKILL ACTIVITY IS DONE **/
        editSkillResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                skillIndexDesc = if(result.data?.getIntExtra("skillDescIndex", 0)!! < 0){
                    val s = result.data?.getIntExtra("skillDescIndex", 0)
                    s
                } else skillIndexDesc

                skillIndex = if(result.data?.getIntExtra("skillIndex", 0)!! > 0){
                    val s = result.data?.getIntExtra("skillIndex", 0)
                    s
                } else skillIndex

                skillOld = if(result.data?.getStringExtra("skillOld")?.length.toString() != "0"){
                    val s = result.data?.getStringExtra("skillOld").toString()
                    s
                } else skillOld

                skillName = if(result.data?.getStringExtra("skillName")?.length.toString() != "0"){
                    val s = result.data?.getStringExtra("skillName").toString()
                    s
                } else skillName

                skillDescription = if(result.data?.getStringExtra("skillDescription")?.length.toString() != "0"){
                    val s = result.data?.getStringExtra("skillDescription").toString()
                    s
                } else skillDescription

                if(skillOld == skillName) _skills!![skillName] = skillDescription
                else {
                    _skills!!.remove(skillOld)
                    _skills!![skillName] = skillDescription
                }

                val tvMOD = findViewById<TextView>(skillIndex!!)
                tvMOD.text = skillName
                val descMOD = findViewById<TextView>(skillIndexDesc!!)
                descMOD.text = skillDescription
            } else {
                skillOld = if(result.data?.getStringExtra("skillOld")?.length.toString() != "0"){
                    val s = result.data?.getStringExtra("skillOld").toString()
                    s
                } else skillOld

                val ln = findViewById<LinearLayout>(R.id.lastLinear)
                ln.removeAllViewsInLayout()
                _skills?.remove(skillOld)
                var indexName = 100
                var indexDesc = -100
                _skills?.toSortedMap()?.forEach {
                    setSkills(it.key, it.value, indexName++, indexDesc--)
                }
            }
        }

        var newSkillName : String? = ""
        var newSkillDesc : String? = ""
        addSkill()
        addSkillResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                newSkillName = if(result.data?.getStringExtra("skillName")?.length.toString() != "0"){
                    val s = result.data?.getStringExtra("skillName").toString()
                    s
                } else newSkillName

                newSkillDesc = if(result.data?.getStringExtra("skillDescription")?.length.toString() != "0"){
                    val s = result.data?.getStringExtra("skillDescription").toString()
                    s
                } else newSkillDesc

                if(newSkillName?.length!=0 && newSkillDesc?.length != 0){
                    _skills?.put(newSkillName!!, newSkillDesc!!)
                    setSkills(newSkillName!!, newSkillDesc!!, indexName++, indexDesc--)
                }

            }
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
                    iv.post { iv.layoutParams = FrameLayout.LayoutParams(w / 3, h - 180) }
                }
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setSkills(skill: String, description: String, indexName : Int, indexDesc : Int) {
        val linearLayout = findViewById<LinearLayout>(R.id.lastLinear)

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
            this,
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
        pencil.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin=5
        }
        pencil.setBackgroundColor(getResources().getColor(R.color.white))

        /** Add the Text and the Arrow to build the header of each skill **/
        expH.addView(arrow, arrowLayoutParams)
        expH.addView(tv)
        expH.addView(pencil)

        /** Prepare the layout for the description **/
        val layout = ExpansionLayout(this)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 20
        }

        /** Text View for the description **/
        val expansionText = TextView(this)
        expansionText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginStart = 5 }
        expansionText.id = indexDesc
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

        pencil.setOnClickListener {
            val intent = Intent(this, EditSkill::class.java)

            intent.putExtra("skillDescIndex", indexDesc)
            intent.putExtra("skillIndex", indexName)
            intent.putExtra("skillOld", skill)
            intent.putExtra("skillName", skill)
            intent.putExtra("skillDescription", description)
            editSkillResultLauncher.launch(intent)
        }

    }

    private fun addSkill() {
        val buttonAddSkill = findViewById<TextView>(R.id.textSkills)

        buttonAddSkill.setOnClickListener {
            val intent = Intent(this, AddSkill::class.java)

            addSkillResultLauncher.launch(intent)
        }
    }

    override fun onBackPressed() {

        val fullName = findViewById<EditText>(R.id.editTextTextPersonName)
        val nickname = findViewById<EditText>(R.id.editTextTextPersonName3)
        val email = findViewById<EditText>(R.id.editTextTextEmailAddress)
        val location = findViewById<EditText>(R.id.editTextLocation)
        val userDesc = findViewById<EditText>(R.id.userDesc)

        val returnIntent = intent

        _fullNameMOD = fullName.text.toString()
        _nicknameMOD = nickname.text.toString()
        _emailMOD = email.text.toString()
        _locationMOD = location.text.toString()
        _aboutUserMOD = userDesc.text.toString()
        val _picture = uri.toString()

        if (_fullNameMOD != null) returnIntent.putExtra("fullName", _fullNameMOD)
        if (_nicknameMOD != null) returnIntent.putExtra("nickname", _nicknameMOD)
        if (_emailMOD != null) returnIntent.putExtra("email", _emailMOD)
        if (_locationMOD != null) returnIntent.putExtra("location", _locationMOD)
        if(_aboutUserMOD != null) returnIntent.putExtra("aboutUser", _aboutUserMOD)
        returnIntent.putExtra("picture", _picture)
        returnIntent.putExtra("skills", _skills as Serializable)

        println("Returned" + _skills)

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
