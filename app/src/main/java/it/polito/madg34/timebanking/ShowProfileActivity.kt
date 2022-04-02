package it.polito.madg34.timebanking

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import java.io.File

class ShowProfileActivity : AppCompatActivity() {
    private var img: Uri = Uri.parse("android.resource://it.polito.madg34.timebanking/"+R.drawable.user_icon)
    private var fullName = "Mario Rossi"
    private var nickname = "Draco123"
    private var email = "mariorossi@general.it"
    private var location = "Corso Castelfidardo, 39, Torino TO"
    private var skills: MutableMap<String, String> = mutableMapOf("Dog Sitter" to "Amo i cani", "Chef" to "Ho vinto la 7 edizione di masterchef",
    "Meccanico" to "Aggiusto macchine d'epoca", "Baby sitter" to "Amo i bamibini, faccio la baby sitter da molti anni")
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    lateinit var fullNameView: TextView;
    lateinit var nicknameView:  TextView;
    lateinit var emailView: TextView;
    lateinit var myLocationView: TextView;
    lateinit var img_view: ImageButton;

    private var h : Int = 0
    private var w : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        fullNameView = findViewById(R.id.fullName)
        nicknameView = findViewById(R.id.nickName)
        emailView = findViewById(R.id.email)
        myLocationView = findViewById(R.id.location)
        img_view = findViewById(R.id.imageButtonShow)

        fullNameView.text = fullName

        nicknameView.text = nickname

        emailView.text = email

        myLocationView.text = location

        img_view.setImageURI(img)

        skills.forEach{
            setSkills(it.key, it.value)
        }

        this.constantScreenLayoutOnScrolling()

        // RICEVUTO IL RISULTATO SOVRASCRIVO
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ){ result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                fullName = if (result.data?.getStringExtra("fullName")?.length.toString() != "0") result.data?.getStringExtra("fullName").toString() else fullName
                nickname = if (result.data?.getStringExtra("nickname")?.length.toString() != "0") result.data?.getStringExtra("nickname").toString() else nickname
                email = if (result.data?.getStringExtra("email")?.length.toString() != "0") result.data?.getStringExtra("email").toString() else email
                location = if (result.data?.getStringExtra("location")?.length.toString() != "0") result.data?.getStringExtra("location").toString() else location
                img = if (result.data?.getStringExtra("picture")?.length.toString() != "0") {
                    var s = result.data?.getStringExtra("picture").toString()
                    Uri.parse(s)
                } else {
                    img
                }


                fullNameView.text = fullName
                nicknameView.text = nickname
                emailView.text = email
                myLocationView.text = location
                img_view.setImageURI(img)
            }
        }
    }

    private fun constantScreenLayoutOnScrolling() {
        val sv = findViewById<ScrollView>(R.id.scrollViewShow)
        val ib = findViewById<ImageButton>(R.id.imageButtonShow)

        sv.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                h = sv.height
                w = sv.width
                ib.post { ib.layoutParams = LinearLayout.LayoutParams(w, h/3) }
                sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setSkills(skill: String, description: String) {
        val linearLayout = findViewById<LinearLayout>(R.id.lastLinear)

        val expH = ExpansionHeader(this)
        val expHLayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        expH.layoutParams = expHLayoutParams
        expH.isToggleOnClick = true

        val arrow = ImageView(this)
        arrow.setImageResource(com.github.florent37.expansionpanel.R.drawable.ic_expansion_header_indicator_grey_24dp)
        with(arrow) {
            expH.id = this.id
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                marginStart = 950
                topMargin = 8
                adjustViewBounds = true
            }
        }
        expH.setExpansionHeaderIndicator(arrow)

        val tv = TextView(this)
        tv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 8
            topMargin = 8
        }
        tv.text = skill
        tv.textSize = 20F
        tv.setTextAppearance(
            this,
            com.google.android.material.R.style.TextAppearance_AppCompat_Body2
        )

        expH.addView(tv)
        expH.addView(arrow)

        val layout = ExpansionLayout(this)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 10
        }
        val expansionText = TextView(this)
        expansionText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        expansionText.id = layout.id
        expansionText.setTextAppearance(
            this,
            com.google.android.material.R.style.TextAppearance_AppCompat_Medium
        )
        expansionText.textSize = 20F
        expansionText.text = description

        layout.addView(expansionText)

        linearLayout.addView(expH)
        linearLayout.addView(layout)

        arrow.setOnClickListener {
            layout.toggle(true)
            if (layout.isExpanded)
                arrow.rotation = 90F
            else arrow.rotation = 0F
        }

    }

    private fun editProfile(){
        val intent = Intent(this, EditProfileActivity::class.java)

        intent.putExtra("fullName",fullName)
        intent.putExtra("nickname",nickname)
        intent.putExtra("email", email)
        intent.putExtra("location",location)
        // intent.putStringArrayListExtra("List", ArrayList<String>(skills))
        intent.putExtra("picture", img.toString())

        resultLauncher.launch(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.pencil_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.pencil ->{
                editProfile()
                //Toast.makeText(this, "Pencil Premuto", Toast.LENGTH_SHORT).show()
                true
            }
            else ->  super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fullName", fullName)
        outState.putString("nickname", nickname)
        outState.putString("email", email)
        outState.putString("location", location)
        outState.putString("img", img.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        fullName = savedInstanceState.getString("fullName","")
        nickname = savedInstanceState.getString("nickname","")
        email = savedInstanceState.getString("email","")
        location = savedInstanceState.getString("location","")
        val s = savedInstanceState.getString("img")
        img = Uri.parse(s)


        fullNameView.text = fullName
        nicknameView.text = nickname
        emailView.text = email
        myLocationView.text = location
        img_view.setImageURI(img)
    }


}