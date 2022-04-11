package it.polito.madg34.timebanking

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

class AddSkill : AppCompatActivity() {

    var _skillName : String ? = ""
    var _skillDescription : String ? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_skill)

        val skillName = findViewById<EditText>(R.id.skillName)
        val skillDesc = findViewById<EditText>(R.id.skillDescription)

        skillName.hint = "New skill name ..."
        skillDesc.hint = "New skill description ..."
    }

    override fun onBackPressed() {
        val returnIntent = intent

        val skillName = findViewById<EditText>(R.id.skillName)
        val skillDescription = findViewById<EditText>(R.id.skillDescription)

        val skillNameMOD = skillName.text.toString()
        var skillDescMOD = skillDescription.text.toString()

        returnIntent.putExtra("skillName", skillNameMOD)
        if(skillDescMOD.isEmpty())
            skillDescMOD = "[No Description]"
        returnIntent.putExtra("skillDescription", skillDescMOD)

        setResult(Activity.RESULT_OK, returnIntent)
        finish()

        super.onBackPressed()
    }
}