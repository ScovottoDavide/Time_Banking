package it.polito.madg34.timebanking

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SkillsViewHolder(v : View) : RecyclerView.ViewHolder(v) {

    private val skillTV : TextView = v.findViewById(R.id.skill)

    fun bind(skillId : String){
        skillTV.text = skillId
    }
}