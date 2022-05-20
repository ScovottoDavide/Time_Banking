package it.polito.madg34.timebanking

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SkillsAdapter(val data : MutableList<String>) : RecyclerView.Adapter<SkillsViewHolder>() {

    lateinit var v : View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.skill_viewholder_layout, parent, false)
        return SkillsViewHolder(v)
    }

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        val item = data[position] // access data item
        Log.d("SKILLSSS", data.toString())
        holder.bind(item)

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = data.size
}