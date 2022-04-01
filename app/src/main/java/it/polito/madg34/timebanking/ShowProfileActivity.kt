package it.polito.madg34.timebanking

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class ShowProfileActivity : AppCompatActivity() {

    private var fullName = "Mario Rossi"
    private var nickname = "Draco123"
    private var email = "mariorossi@general.it"
    private var location = "Corso Castelfidardo, 39, Torino TO"
    private var skills: MutableList<String> = mutableListOf("Dog Sitter", "Chef")
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        var fullNameView = findViewById<TextView>(R.id.fullName)
        var nicknameView = findViewById<TextView>(R.id.nickName)
        var emailView = findViewById<TextView>(R.id.email)
        var myLocationView = findViewById<TextView>(R.id.location)

        fullNameView.text = fullName

        nicknameView.text = nickname

        emailView.text = email

        myLocationView.text = location

        /** Expand Skill to get description **/
        val layout = findViewById<ExpansionLayout>(R.id.expansionLayout)
        val layoutExp = findViewById<LinearLayout>(R.id.expansionLinear)
        val arrow = findViewById<ImageView>(R.id.headerIndicator)

        //val lastLinear = findViewById<LinearLayout>(R.id.lastLinear)

        skills.forEachIndexed { index , element ->
            var x = TextView(this)
            x.text = "Skill${index+1}: ${element}\nDescription: Provide some description"
            x.setTextSize(20F)
            layoutExp.addView(x, index)
        }



       /* skills.forEach {

            val newExHeader = ExpansionHeader(this)
            val newExLayout = ExpansionLayout(this)
            val image = ImageView(this)
            val x = TextView(this)

            image.setImageResource(com.github.florent37.expansionpanel.R.drawable.ic_expansion_header_indicator_grey_24dp)

            x.text = it
            x.setTextSize(20F)


            lastLinear.addView(newExHeader)
            lastLinear.addView(newExLayout)


            newExHeader.addView(x)
            newExHeader.addView(image)

            image.setOnClickListener{
                newExLayout.toggle(true)
                if(newExLayout.isExpanded)
                    image.rotation = 90F
                else image.rotation = 0F
            }

        }*/

        arrow.setOnClickListener {
            layout.toggle(true)
            if(layout.isExpanded)
                arrow.rotation = 90F
            else arrow.rotation = 0F
        }



        // RICEVUTO IL RISULTATO SOVRASCRIVO
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()
        ){ result : ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                fullName = if (result.data?.getStringExtra("fullName")?.length.toString() != "0") result.data?.getStringExtra("fullName").toString() else fullName
                nickname = if (result.data?.getStringExtra("nickname")?.length.toString() != "0") result.data?.getStringExtra("nickname").toString() else nickname
                email = if (result.data?.getStringExtra("email")?.length.toString() != "0") result.data?.getStringExtra("email").toString() else email
                location = if (result.data?.getStringExtra("location")?.length.toString() != "0") result.data?.getStringExtra("location").toString() else location


                fullNameView.text = fullName

                nicknameView.text = nickname

                emailView.text = email

                myLocationView.text = location

            }

        }


    }

    private fun editProfile(){

        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("fullName",fullName)
        intent.putExtra("nickname",nickname)
        intent.putExtra("email", email)
        intent.putExtra("location",location)
        // intent.putStringArrayListExtra("List", ArrayList<String>(skills))

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
}