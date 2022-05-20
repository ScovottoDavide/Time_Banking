package it.polito.madg34.timebanking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class TimeSlotAdapter(val data : MutableList<TimeSlot>) : RecyclerView.Adapter<TimeSlotViewHolder>() {

    lateinit var v : View
    lateinit var vmTimeSlot : TimeSlotViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return TimeSlotViewHolder(v)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val item = data[position] // access data item
        holder.bind(item)

        vmTimeSlot = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        // Click Listener on the whole Card
        holder.itemView.setOnClickListener{
            val bundle = Bundle()
            bundle.putInt("index", position)
            vmTimeSlot.currentShownAdv = data[position]
            Navigation.findNavController(holder.itemView).navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment2, bundle)
        }

        val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.deleteCard)
        deleteButton.setOnClickListener {
            vmTimeSlot.removeAdv(data[position])
            data.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
            Snackbar.make(holder.itemView, "Service successfully removed!", Snackbar.LENGTH_LONG).show()
        }

        val editCardViewButton : ImageButton = holder.itemView.findViewById(R.id.editCard)
        // Click Listener on the edit Button of the Card
        editCardViewButton.setOnClickListener {
            vmTimeSlot.currentShownAdv = data[position]
            Navigation.findNavController(holder.itemView).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2, bundleOf("index" to position))
        }
    }

    override fun getItemCount(): Int = data.size

}