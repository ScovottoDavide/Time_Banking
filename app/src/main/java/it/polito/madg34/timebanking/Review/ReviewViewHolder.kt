package it.polito.madg34.timebanking.Review

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    private val serviceImage: CircleImageView = v.findViewById(R.id.serviceImage)
    private val serviceImage2: ImageView = v.findViewById(R.id.serviceImage2)
    private val serviceTitle: TextView = v.findViewById(R.id.serviceTitle)
    private val ratingBar: RatingBar = v.findViewById(R.id.requesterRatingBar)
    private val relatedSkill: TextView = v.findViewById(R.id.relatedSkill)
    private val score: TextView = v.findViewById(R.id.Score)
    private val nicknameTv: TextView = v.findViewById(R.id.suffix)
    private val reviewDate: TextView = v.findViewById(R.id.reviewDate)
    private val comment: TextInputEditText = v.findViewById(R.id.commentReview)

    fun bind(review: Review, timeslot : TimeSlot, userImg: String?, nickname : String) {
        if (userImg.isNullOrEmpty()) {
            serviceImage.visibility = View.INVISIBLE
            serviceImage2.visibility = View.VISIBLE
            serviceImage2.setImageResource(R.drawable.time_management)
        } else {
            serviceImage2.visibility = View.GONE
            serviceImage.visibility = View.VISIBLE
            Glide.with(this.itemView).load(userImg).diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform().into(serviceImage)
        }
        serviceTitle.text = timeslot.title
        ratingBar.rating = review.score
        relatedSkill.text = timeslot.related_skill
        score.text = review.score.toString()
        nicknameTv.text = nickname
        val time = review.timestamp?.let { DateUtil.formatTime(it) }
        val date = review.timestamp?.let { DateUtil.formatDate(it) }
        reviewDate.text = "$date $time"
        if(review.comment.isNullOrEmpty())
            comment.setText("No comment..")
        else
            comment.setText(review.comment)
    }


    object DateUtil {
        fun formatTime(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }

        fun formatDate(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }
    }
}