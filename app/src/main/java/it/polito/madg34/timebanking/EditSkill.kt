package it.polito.madg34.timebanking

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class EditSkill : AppCompatActivity() {

    var _index : Int = 0
    var _indexDesc : Int = 0
    var _skillDescription : String ? = ""
    var _skillName : String ? = ""
    var _skillOld : String ? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_skill)

        val intent = intent

        _skillName = intent.getStringExtra("skillName")
        _skillDescription = intent.getStringExtra("skillDescription")
        _index = intent.getIntExtra("skillIndex", -1)
        _indexDesc = intent.getIntExtra("skillDescIndex", -1)
        _skillOld = intent.getStringExtra("skillOld")

        val tv = findViewById<TextView>(R.id.skillName)
        val editDesc = findViewById<EditText>(R.id.skillDescription)

        tv.text = _skillName
        editDesc.setText(_skillDescription)

        val deleteButton = findViewById<Button>(R.id.deleteSkill)
        deleteButton.setOnClickListener {

            intent.putExtra("skillOld", _skillOld)
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val returnIntent = intent
        val skillName = findViewById<EditText>(R.id.skillName)
        val skillDescription = findViewById<EditText>(R.id.skillDescription)
        val skillNameMOD = skillName.text.toString()
        var skillDescriptionMOD = skillDescription.text.toString()
        if(skillNameMOD.isNotEmpty()){
            if(_index >= 0){
                returnIntent.putExtra("skillName", skillNameMOD)
                if(skillDescriptionMOD.isEmpty())
                    skillDescriptionMOD = "[No Description]"
                returnIntent.putExtra("skillDescription", skillDescriptionMOD)
                returnIntent.putExtra("skillOld", _skillOld)
                returnIntent.putExtra("skillIndex", _index)
                returnIntent.putExtra("skillDescIndex", _indexDesc)
            }
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
            super.onBackPressed()
        }else
            Toast.makeText(this, "To delete skill press the button Delete", Toast.LENGTH_SHORT).show()
    }
}