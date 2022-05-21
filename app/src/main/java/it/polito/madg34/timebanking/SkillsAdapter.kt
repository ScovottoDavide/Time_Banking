package it.polito.madg34.timebanking

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.get
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

class SkillsAdapter(var data : MutableList<String>, val listStringsAdvs : MutableList<Skills>) : RecyclerView.Adapter<SkillsViewHolder>() {

    lateinit var v : View
    lateinit var vmSkills : SkillsViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {
        v = LayoutInflater.from(parent.context).inflate(R.layout.skill_viewholder_layout, parent, false)
        return SkillsViewHolder(v)
    }

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {
        val item = data[position] // access data item
        holder.bind(item)

        vmSkills = ViewModelProvider(holder.itemView.context as ViewModelStoreOwner).get()

        holder.itemView.setOnClickListener {
            vmSkills.fromHome.value = true
            vmSkills.stringAdvs = listStringsAdvs[position].relatedAdvs
            vmSkills.loadSkillAdvs()
            Navigation.findNavController(holder.itemView).navigate(R.id.action_skillsFragment_to_timeSlotListFragment)
        }
    }

    override fun getItemCount(): Int = data.size
}