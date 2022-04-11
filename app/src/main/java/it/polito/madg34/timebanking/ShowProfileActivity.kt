package it.polito.madg34.timebanking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Text
import java.io.Serializable

class ShowProfileActivity : AppCompatActivity() {

    private lateinit var sharedPref : SharedPreferences
    private lateinit var  gson : Gson

    private var img: Uri = Uri.parse("android.resource://it.polito.madg34.timebanking/"+R.drawable.user_icon)
    private var fullName = "Mario Rossi"
    private var nickname = "Draco123"
    private var email = "mariorossi@general.it"
    private var location = "Corso Castelfidardo, 39, Torino TO"
    private var aboutUser = "Salve a tutti, mi chiamo Massimo e ho 20 anni. Sono una persona disponibile" +
                            " e sono qui per offrire il mio tempo libero e le mie capacità. Offrire aiuto" +
                            " è una cosa che adoro e non esitate a contattarmi."
    private var skills: MutableMap<String, String>? = mutableMapOf(
        "Dog Sitter" to "Amo i cani",
        "Chef" to "Ho vinto la 7 edizione di masterchef",
        "Meccanico" to "Aggiusto macchine d'epoca",
        "Baby sitter" to "Amo i bamibini, faccio la baby sitter da molti anniiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"
    ).toSortedMap()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    lateinit var fullNameView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var myLocationView: TextView
    lateinit var userDesc : TextView
    lateinit var img_view: ImageView

    private var h: Int = 0
    private var w: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        setSupportActionBar(findViewById(R.id.my_toolbar))
        val ab = supportActionBar
        if (ab != null) ab.title = "Time Banking"

        fullNameView = findViewById(R.id.fullName)
        nicknameView = findViewById(R.id.nickName)
        emailView = findViewById(R.id.email)
        myLocationView = findViewById(R.id.location)
        userDesc = findViewById(R.id.userDesc)
        img_view = findViewById(R.id.imageUsr)

        gson = Gson()
        sharedPref = getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE) ?: return
        restore()

//        sharedPref = getSharedPreferences("package it.polito.madg34.timebanking.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        editor.clear().apply()

        fullNameView.text = fullName
        nicknameView.text = nickname
        emailView.text = email
        myLocationView.text = location
        userDesc.text = aboutUser
        img_view.setImageURI(img)

        skills?.forEach {
            setSkills(it.key, it.value)
        }

        this.constantScreenLayoutOnScrolling()

        // RICEVUTO IL RISULTATO SOVRASCRIVO
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {

                fullName =
                    if (result.data?.getStringExtra("fullName")?.length.toString() != "0") {
                        val s = result.data?.getStringExtra("fullName").toString()
                        val serialized = gson.toJson(s)
                        sharedPref.edit().putString("FULLNAME",serialized).apply()
                        s
                    } else fullName

                nickname =
                    if (result.data?.getStringExtra("nickname")?.length.toString() != "0") {
                        val s = result.data?.getStringExtra("nickname").toString()
                        val serialized = gson.toJson(s)
                        sharedPref.edit().putString("NICKNAME",serialized).apply()
                        s
                    } else nickname

                email =
                    if (result.data?.getStringExtra("email")?.length.toString() != "0") {
                        val s = result.data?.getStringExtra("email").toString()
                        val serialized = gson.toJson(s)
                        sharedPref.edit().putString("EMAIL",serialized).apply()
                        s
                    } else email

                location =
                    if (result.data?.getStringExtra("location")?.length.toString() != "0") {
                        val s = result.data?.getStringExtra("location").toString()
                        val serialized = gson.toJson(s)
                        sharedPref.edit().putString("LOCATION",serialized).apply()
                        s
                    } else location

                aboutUser =
                    if (result.data?.getStringExtra("aboutUser")?.length.toString() != "0") {
                        val s = result.data?.getStringExtra("aboutUser").toString()
                        val serialized = gson.toJson(s)
                        sharedPref.edit().putString("ABOUTUSER",serialized).apply()
                        s
                    } else aboutUser

                img =
                    if (result.data?.getStringExtra("picture")?.length.toString() != "0") {
                        val s = result.data?.getStringExtra("picture").toString()
                        val serialized = gson.toJson(s)
                        sharedPref.edit().putString("IMG",serialized).apply()
                        Uri.parse(s)
                    } else img

                val res = result.data?.getSerializableExtra("skills") as MutableMap<String, String>?
                skills = if(res?.size!! > 0){
                    val serialized = gson.toJson(res)
                    sharedPref.edit().putString("SKILLS",serialized).apply()
                    res
                } else {
                    val serialized = gson.toJson(res)
                    sharedPref.edit().putString("SKILLS",serialized).apply()
                    null
                }

                fullNameView.text = fullName
                nicknameView.text = nickname
                emailView.text = email
                myLocationView.text = location
                userDesc.text = aboutUser
                val ln = findViewById<LinearLayout>(R.id.lastLinear)
                ln.removeAllViewsInLayout()
                skills?.toSortedMap()?.forEach {
                    setSkills(it.key, it.value)
                }
                img_view.setImageURI(img)
            }
        }
    }

    private fun restore(){

        if (sharedPref.contains("FULLNAME")){
            fullName = gson.fromJson(sharedPref.getString("FULLNAME", "Ciao"), String::class.java)
        }
        if (sharedPref.contains("NICKNAME")){
            nickname = gson.fromJson(sharedPref.getString("NICKNAME", ""), String::class.java)
        }
        if (sharedPref.contains("EMAIL")){
            email = gson.fromJson(sharedPref.getString("EMAIL", ""), String::class.java)
        }
        if (sharedPref.contains("LOCATION")){
            location = gson.fromJson(sharedPref.getString("LOCATION", ""), String::class.java)
        }
        if (sharedPref.contains("ABOUTUSER")){
            aboutUser = gson.fromJson(sharedPref.getString("ABOUTUSER", ""), String::class.java)
        }
        if (sharedPref.contains("SKILLS")){
            skills = gson.fromJson(sharedPref.getString("SKILLS", ""), object :
                TypeToken<MutableMap<String, String>>() {}.type)
            skills?.toSortedMap()
        }
        if (sharedPref.contains("IMG")){
            val s = gson.fromJson(sharedPref.getString("IMG", ""), String::class.java)
            img = Uri.parse(s)
        }

    }

    private fun constantScreenLayoutOnScrolling() {
        val sv = findViewById<ScrollView>(R.id.scrollViewShow)
        val constraintL = findViewById<ConstraintLayout>(R.id.landLayout)
        val iv = findViewById<ImageView>(R.id.imageUsr)

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
        tv.setTextAppearance(this, com.google.android.material.R.style.TextAppearance_AppCompat_Body2)

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
        expansionText.setTextAppearance(this, com.google.android.material.R.style.TextAppearance_AppCompat_Body1)
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

    private fun editProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)

        intent.putExtra("fullName", fullName)
        intent.putExtra("nickname", nickname)
        intent.putExtra("email", email)
        intent.putExtra("location", location)
        intent.putExtra("aboutUser", aboutUser)
        intent.putExtra("skills", skills as Serializable?)
        intent.putExtra("picture", img.toString())

        resultLauncher.launch(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.pencil_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.pencil -> {
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fullName", fullName)
        outState.putString("nickname", nickname)
        outState.putString("email", email)
        outState.putString("location", location)
        outState.putString("aboutUser", aboutUser)
        outState.putSerializable("skills", skills as Serializable?)
        outState.putString("img", img.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        fullName = savedInstanceState.getString("fullName", "")
        nickname = savedInstanceState.getString("nickname", "")
        email = savedInstanceState.getString("email", "")
        location = savedInstanceState.getString("location", "")
        aboutUser = savedInstanceState.getString("aboutUser", "")
        skills = savedInstanceState.getSerializable("skills") as MutableMap<String, String>?

        val s = savedInstanceState.getString("img")
        img = Uri.parse(s)


        fullNameView.text = fullName
        nicknameView.text = nickname
        emailView.text = email
        myLocationView.text = location
        userDesc.text = aboutUser
        img_view.setImageURI(img)
    }

}