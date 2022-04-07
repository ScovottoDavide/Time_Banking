package it.polito.madg34.timebanking

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView

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
    }

    override fun onBackPressed() {
        val returnIntent = intent
        if(_index >= 0){
            val skillName = findViewById<EditText>(R.id.skillName)
            val skillDescription = findViewById<EditText>(R.id.skillDescription)

            val skillNameMOD = skillName.text.toString()
            val skillDescriptionMOD = skillDescription.text.toString()

            returnIntent.putExtra("skillName", skillNameMOD)
            returnIntent.putExtra("skillDescription", skillDescriptionMOD)
            returnIntent.putExtra("skillOld", _skillOld)
            returnIntent.putExtra("skillIndex", _index)
            returnIntent.putExtra("skillDescIndex", _indexDesc)
        }
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
        super.onBackPressed()
    }
}