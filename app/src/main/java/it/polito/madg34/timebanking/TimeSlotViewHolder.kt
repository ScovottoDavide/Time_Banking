package it.polito.madg34.timebanking

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeSlotViewHolder(v : View) : RecyclerView.ViewHolder(v){

    private val courseImage : ImageView = v.findViewById(R.id.idIVCourseImage)
    private val courseName : TextView = v.findViewById(R.id.idTVCourseName)
    private val courseRating : TextView = v.findViewById(R.id.idTVCourseRating)

    fun bind(item : TimeSlot) {
        courseImage.setImageResource(R.drawable.user)
        courseName.text = item.title
        courseRating.text = item.location
    }


}