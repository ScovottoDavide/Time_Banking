package it.polito.madg34.timebanking.Messages

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.installations.Utils
import de.hdodenhof.circleimageview.CircleImageView

import it.polito.madg34.timebanking.R
import java.text.SimpleDateFormat
import java.util.*


class ReceivedMessageHolder(v : View) : RecyclerView.ViewHolder(v) {
    lateinit var messageText: TextView
    lateinit var timeText: TextView
    lateinit var nameText: TextView
    lateinit var dateText: TextView
    lateinit var profileImage: ImageView

    fun bind(message: Message, otherUserImage : String?, nickname : String?) {
        nameText = itemView.findViewById<View>(R.id.text_gchat_user_other) as TextView
        timeText = itemView.findViewById<View>(R.id.text_gchat_timestamp_other) as TextView
        messageText = itemView.findViewById<View>(R.id.text_gchat_message_other) as TextView
        dateText = itemView.findViewById<View>(R.id.text_gchat_date_other) as TextView
        profileImage = itemView.findViewById<View>(R.id.image_gchat_profile_other) as CircleImageView

        messageText.setText(message.messageContent)

        // Format the stored timestamp into a readable String using method.
        timeText.setText(DateUtil.formatTime(message.timeStamp))
        dateText.setText(DateUtil.formatDate(message.timeStamp))

        nameText.setText(nickname)

        // Insert the profile image from the URL into the ImageView.
        if(!otherUserImage.isNullOrEmpty()){
            Glide.with(this.itemView).load(otherUserImage).diskCacheStrategy( DiskCacheStrategy.ALL ).dontTransform().into(profileImage)
        }else{
            profileImage.setImageResource(R.drawable.time_management)
        }
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