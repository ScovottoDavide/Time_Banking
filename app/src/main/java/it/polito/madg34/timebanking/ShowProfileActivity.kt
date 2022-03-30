package it.polito.madg34.timebanking

import android.app.ActionBar
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.expansionpanel.ExpansionHeader
import com.github.florent37.expansionpanel.ExpansionLayout
import java.util.*


class ShowProfileActivity : AppCompatActivity() {

    val fullName = "Mario Rossi"
    val nickname = "Draco123"
    val email = "mariorossi@general.it"
    val location = "Corso Castelfidardo, 39, Torio TO"
    val skills: Array<String> = arrayOf("Dog Sitter", "Chef")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        val fullNameView = findViewById<TextView>(R.id.fullName)
        fullNameView.text = fullName

        val nicknameView = findViewById<TextView>(R.id.nickName)
        nicknameView.text = nickname

        val emailView = findViewById<TextView>(R.id.email)
        emailView.text = email

        val myLocationView = findViewById<TextView>(R.id.location)
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
    }
}