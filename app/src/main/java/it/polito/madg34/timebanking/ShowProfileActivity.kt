package it.polito.madg34.timebanking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.github.florent37.expansionpanel.ExpansionLayout

class ShowProfileActivity : AppCompatActivity() {

    val fullName = "Mario Rossi"
    val nickname = "Draco123"
    val email = "mariorossi@general.it"
    val location = "Corso Castelfidardo, 39, Torio TO"
    val skills: Array<String> = arrayOf("Dog sitter", "Chef")

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
        val arrow = findViewById<ImageView>(R.id.headerIndicator)
        arrow.setOnClickListener {
            layout.toggle(true)
            if(layout.isExpanded)
                arrow.rotation = 90F
            else arrow.rotation = 0F
        }
    }
}