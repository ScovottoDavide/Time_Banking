package it.polito.madg34.timebanking.Chat

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot


class ChatViewHolder(v : View) : RecyclerView.ViewHolder(v) {
    private val serviceImage : CircleImageView = v.findViewById(R.id.serviceImage)
    private val serviceImage2 : ImageView = v.findViewById(R.id.serviceImage2)
    private val serviceTitle : TextView = v.findViewById(R.id.serviceTitle)
    private val serviceLocation : TextView = v.findViewById(R.id.serviceLocation)
    private val serviceDate : TextView = v.findViewById(R.id.serviceDate)
    private val statusButton : MaterialButton = v.findViewById(R.id.statusButton)

    @SuppressLint("ResourceAsColor")
    fun bind(item : TimeSlot, userImg : String?, status : Boolean) {
        if(userImg.isNullOrEmpty()){
            serviceImage.visibility = View.INVISIBLE
            serviceImage2.visibility = View.VISIBLE
            serviceImage2.setImageResource(R.drawable.time_management)
        }
        else {
            serviceImage2.visibility = View.GONE
            serviceImage.visibility = View.VISIBLE
            Glide.with(this.itemView).load(userImg).diskCacheStrategy( DiskCacheStrategy.ALL ).dontTransform().into(serviceImage)
        }

        if(status){
            val cls = ColorStateList.valueOf(Color.parseColor("#008000"))
            statusButton.strokeColor = cls
            statusButton.setTextColor(cls)
            statusButton.setText("Accepted")
        } else {
            val cls = ColorStateList.valueOf(Color.parseColor("#ffa500"))
            statusButton.strokeColor = cls
            statusButton.setTextColor(cls)
            statusButton.setText("Pending")
        }
        serviceTitle.text = item.title
        serviceLocation.text = item.location
        serviceDate.text = item.date
    }
}