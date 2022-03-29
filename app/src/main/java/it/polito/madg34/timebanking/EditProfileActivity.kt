package it.polito.madg34.timebanking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast

class EditProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val userImage : ImageButton = findViewById(R.id.imageButtonShow)

        userImage.setOnClickListener{
            Toast.makeText(this, "Open camera not yet implemented", Toast.LENGTH_SHORT).show()
        }

    }
}