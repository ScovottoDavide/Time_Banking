package it.polito.madg34.timebanking

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView

class TimeSlotViewHolder(v : View) : RecyclerView.ViewHolder(v){

    private val serviceImage : CircleImageView = v.findViewById(R.id.serviceImage)
    private val serviceImage2 : ImageView = v.findViewById(R.id.serviceImage2)
    private val serviceTitle : TextView = v.findViewById(R.id.serviceTitle)
    private val serviceLocation : TextView = v.findViewById(R.id.serviceLocation)
    private val serviceDate : TextView = v.findViewById(R.id.serviceDate)

    @SuppressLint("ResourceAsColor")
    fun bind(item : TimeSlot, userImg : String?) {
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
    }
}