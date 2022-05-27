package it.polito.madg34.timebanking.Messages

import it.polito.madg34.timebanking.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class SentMessageHolder(v : View) : RecyclerView.ViewHolder(v) {
    lateinit var messageText: TextView
    lateinit var timeText:TextView
    lateinit var dateText : TextView

    fun bind(message: Message) {

        messageText = itemView.findViewById(R.id.text_gchat_message_me)
        timeText = itemView.findViewById(R.id.text_gchat_timestamp_me)
        dateText = itemView.findViewById(R.id.text_gchat_date_me)
        messageText.setText(message.messageContent)

        // Format the stored timestamp into a readable String using method.
        timeText.setText(DateUtil.formatTime(message.timeStamp))
        dateText.setText(DateUtil.formatDate(message.timeStamp))
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