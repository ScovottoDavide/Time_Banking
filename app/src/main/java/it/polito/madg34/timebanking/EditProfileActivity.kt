package it.polito.madg34.timebanking

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*

class EditProfileActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    var _fullNameMOD: String? = null
    var _nicknameMOD: String? = null
    var _emailMOD: String? = null
    var _locationMOD: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val userImage  = findViewById<ImageView>(R.id.userImage)
        /*val plus  = findViewById<ImageButton>(R.id.plus)

        plus.setOnClickListener{
            Toast.makeText(this, "Open camera not yet implemented", Toast.LENGTH_SHORT).show()
        }*/


        val intent = intent
        var _fullName = intent.getStringExtra("fullName")
        var _nickname = intent.getStringExtra("nickname")
        var _email = intent.getStringExtra("email")
        var _location = intent.getStringExtra("location")

        var fullName  = findViewById<EditText>(R.id.editTextTextPersonName)
        var nickname  = findViewById<EditText>(R.id.editTextTextPersonName3)
        var email  = findViewById<EditText>(R.id.editTextTextEmailAddress)
        var location  = findViewById<EditText>(R.id.editTextLocation)

        fullName.hint = _fullName
        nickname.hint = _nickname
        email.hint = _email
        location.hint = _location








    }


    override fun onBackPressed() {

        var fullName  = findViewById<EditText>(R.id.editTextTextPersonName)
        var nickname  = findViewById<EditText>(R.id.editTextTextPersonName3)
        var email  = findViewById<EditText>(R.id.editTextTextEmailAddress)
        var location  = findViewById<EditText>(R.id.editTextLocation)

        val returnIntent = intent

        _fullNameMOD = fullName.text.toString()
        _nicknameMOD = nickname.text.toString()
        _emailMOD = email.text.toString()
        _locationMOD = location.text.toString()

        if (_fullNameMOD != null) returnIntent.putExtra("fullName", _fullNameMOD)
        if(_nicknameMOD!= null) returnIntent.putExtra("nickname", _nicknameMOD)
        if(_emailMOD != null) returnIntent.putExtra("email", _emailMOD)
        if (_locationMOD != null) returnIntent.putExtra("location", _locationMOD)
        setResult(Activity.RESULT_OK,returnIntent)
        finish()

        super.onBackPressed()



    }

    public fun showPopup(v : View){
       val popup = PopupMenu(this,v)
        popup.setOnMenuItemClickListener (this)
        popup.inflate(R.menu.popup_menu)
        popup.show()
   }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            return when (item.itemId){
               R .id.select ->{
                    Toast.makeText(this, "Select", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.camera ->{
                    Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show()
                    true
                }
                else ->  super.onOptionsItemSelected(item)

            }
        }else {return false}


    }

}