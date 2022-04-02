package it.polito.madg34.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream


class EditProfileActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {


    var _fullNameMOD: String? = null
    var _nicknameMOD: String? = null
    var _emailMOD: String? = null
    var _locationMOD: String? = null

    private lateinit var takePicture: ActivityResultLauncher<Intent>
    private lateinit var takePictureGallery: ActivityResultLauncher<String>
    private lateinit var userImage: ImageView
    private lateinit var bitmap : Bitmap
    private lateinit var uri : Uri





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        /*val plus  = findViewById<ImageButton>(R.id.plus)

        plus.setOnClickListener{
            Toast.makeText(this, "Open camera not yet implemented", Toast.LENGTH_SHORT).show()
        }*/


        val intent = intent
        var _fullName = intent.getStringExtra("fullName")
        var _nickname = intent.getStringExtra("nickname")
        var _email = intent.getStringExtra("email")
        var _location = intent.getStringExtra("location")
        var _pictureString = intent.getStringExtra("picture")

        uri = Uri.parse(_pictureString)


        var fullName  = findViewById<EditText>(R.id.editTextTextPersonName)
        var nickname  = findViewById<EditText>(R.id.editTextTextPersonName3)
        var email  = findViewById<EditText>(R.id.editTextTextEmailAddress)
        var location  = findViewById<EditText>(R.id.editTextLocation)
        userImage  = findViewById<ImageView>(R.id.userImage)

        fullName.hint = _fullName
        nickname.hint = _nickname
        email.hint = _email
        location.hint = _location
        userImage.setImageURI(uri)

        takePicture  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if(result.resultCode == Activity.RESULT_OK){
                handleCameraImage(result.data)

            }
        }

        takePictureGallery  = registerForActivityResult(ActivityResultContracts.GetContent()){
            uri = it
            userImage.setImageURI(uri)

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
        var _picture = uri.toString()

        if (_fullNameMOD != null) returnIntent.putExtra("fullName", _fullNameMOD)
        if(_nicknameMOD!= null) returnIntent.putExtra("nickname", _nicknameMOD)
        if(_emailMOD != null) returnIntent.putExtra("email", _emailMOD)
        if (_locationMOD != null) returnIntent.putExtra("location", _locationMOD)
        if (_picture != null) returnIntent.putExtra("picture", _picture)

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

    private fun dispatchTakePictureIntent() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePicture.launch(takePictureIntent)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    private fun dispatchTakeGalleryPictureIntent() {
        try {

            takePictureGallery.launch("image/*")
        } catch (e: ActivityNotFoundException){
            //......
        }

    }




    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if (item != null) {
            return when (item.itemId){
               R .id.select ->{
                   dispatchTakeGalleryPictureIntent()
                    true
                }
                R.id.camera ->{
                    dispatchTakePictureIntent()
                    true
                }
                else ->  super.onOptionsItemSelected(item)

            }
        }else {return false}

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