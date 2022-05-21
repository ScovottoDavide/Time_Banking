package it.polito.madg34.timebanking

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class TimeSlotViewHolder(v : View) : RecyclerView.ViewHolder(v){

    private val serviceImage : CircleImageView = v.findViewById(R.id.serviceImage)
    private val serviceTitle : TextView = v.findViewById(R.id.serviceTitle)
    private val serviceLocation : TextView = v.findViewById(R.id.serviceLocation)
    private val serviceDate : TextView = v.findViewById(R.id.serviceDate)

    fun bind(item : TimeSlot, userImg : String?) {
        if(userImg == null)
            serviceImage.setImageResource(R.drawable.time_management)
        else Glide.with(this.itemView).load(userImg).into(serviceImage)
        serviceTitle.text = item.title
        serviceLocation.text = item.location
        serviceDate.text = item.date
    }
}