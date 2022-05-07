package it.polito.madg34.timebanking

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView

class TimeSlotViewHolder(v : View) : RecyclerView.ViewHolder(v){

    private val serviceImage : ImageView = v.findViewById(R.id.serviceImage)
    private val serviceTitle : TextView = v.findViewById(R.id.serviceTitle)
    private val serviceLocation : TextView = v.findViewById(R.id.serviceLocation)
    private val serviceDate : TextView = v.findViewById(R.id.serviceDate)

    fun bind(item : TimeSlot) {
        serviceImage.setImageResource(R.drawable.time_management)
        serviceTitle.text = item.title
        serviceLocation.text = item.location
        serviceDate.text = item.date
    }
}