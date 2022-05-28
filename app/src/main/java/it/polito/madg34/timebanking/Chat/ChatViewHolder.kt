package it.polito.madg34.timebanking.Chat

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.madg34.timebanking.R



class ChatViewHolder(v : View) : RecyclerView.ViewHolder(v) {
    private val img = v.findViewById<CircleImageView>(R.id.userImage_chat)
    private val chatT = v.findViewById<TextView>(R.id.user_chat_name)


    fun bind(item : String, userImg : String?) {
        if(!userImg.isNullOrEmpty()){
            Glide.with(this.itemView).load(userImg).diskCacheStrategy( DiskCacheStrategy.ALL ).dontTransform().into(img)
        }else{
            img.setImageResource(R.drawable.time_management)
        }
        chatT.text = item
    }
}