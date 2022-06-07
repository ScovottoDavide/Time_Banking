package it.polito.madg34.timebanking.Review

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlot
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel
import java.lang.Exception

class ReviewAdapter(val reviewList : List<Review>) : RecyclerView.Adapter<ReviewViewHolder>() {
    lateinit var v : View

    lateinit var vmProfile : ProfileViewModel
    lateinit var vmTimeSlot: TimeSlotViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        v = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_review_layout, parent, false)
        return ReviewViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        vmProfile = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()
        vmTimeSlot = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        val review = reviewList[position]

        vmProfile.loadChatImage(review.reviewer!!).addOnSuccessListener {
            if (it != null){
                vmTimeSlot.getAdvForReview(review.id_adv!!)?.addSnapshotListener(EventListener{ value , e->
                    Log.d("STAMPA1", value?.toTimeSlotObject().toString())
                    if(value != null){
                        holder.bind(review, value.toTimeSlotObject()!!, vmProfile.chatImage.value, vmProfile.nickname )
                    }
                })
            }

        }

    }

    override fun getItemCount(): Int = reviewList.size

    private fun DocumentSnapshot.toTimeSlotObject(): TimeSlot? {
        return try {
            val id = get("ID") as String
            val title = get("TITLE") as String
            val description = get("DESCRIPTION") as String
            val date = get("DATE") as String
            val time = get("TIME") as String
            val duration = get("DURATION") as String
            val location = get("LOCATION") as String
            val email = get("PUBLISHED_BY") as String
            val related_skill = get("RELATED_SKILL") as String
            val available = get("AVAILABLE") as Long
            val refused = get("REFUSED") as String
            val accepted = get("ACCEPTED") as String
            val index = get("INDEX") as Long
            val reviews = get("REVIEWS") as String

            TimeSlot(
                id, title, description, date, time, duration, location, email, related_skill,
                index.toInt(), available.toInt(), refused, accepted, reviews
            )

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}