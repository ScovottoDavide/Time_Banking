package it.polito.madg34.timebanking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*

class EditProfileActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val userImage  = findViewById<ImageView>(R.id.userImage)
        /*val plus  = findViewById<ImageButton>(R.id.plus)

        plus.setOnClickListener{
            Toast.makeText(this, "Open camera not yet implemented", Toast.LENGTH_SHORT).show()
        }*/



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
                R.id.select ->{
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