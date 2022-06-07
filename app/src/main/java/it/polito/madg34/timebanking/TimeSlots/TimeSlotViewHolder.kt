package it.polito.madg34.timebanking.TimeSlots

import android.annotation.SuppressLint
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.madg34.timebanking.R
import org.w3c.dom.Text

class TimeSlotViewHolder(v : View) : RecyclerView.ViewHolder(v){

    private val serviceImage : CircleImageView = v.findViewById(R.id.serviceImage)
    private val serviceImage2 : ImageView = v.findViewById(R.id.serviceImage2)
    private val serviceTitle : TextView = v.findViewById(R.id.serviceTitle)
    private val serviceLocation : TextView = v.findViewById(R.id.serviceLocation)
    private val serviceDate : TextView = v.findViewById(R.id.serviceDate)
    private val serviceSkill : TextView = v.findViewById(R.id.serviceSkill)
    private val earnTime : TextView = v.findViewById(R.id.earnTime)
    private val userNicknameTV : TextView = v.findViewById(R.id.publishedTV)


    @SuppressLint("ResourceAsColor")
    fun bind(item : TimeSlot, userImg : String?, userNickname: String?) {
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
        serviceTitle.text = item.title
        serviceLocation.text = item.location
        serviceDate.text = item.date
        serviceSkill.text = item.related_skill
        earnTime.text = item.duration
        userNicknameTV.text = userNickname
    }
}