package it.polito.madg34.timebanking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SkillsFragment : Fragment() {

    val vmSkills : SkillsViewModel by activityViewModels()

    private var skills : MutableMap<String, Skills> = mutableMapOf()
    lateinit var emptyView: TextView
    lateinit var skillsRV: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.skillsfragment_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vmSkills.getAllSkillsVM().observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()){
                skills = it
                if (skills.isEmpty()) {
                    emptyView = view.findViewById(R.id.emptyListTV)
                    emptyView.visibility = View.VISIBLE
                } else {
                    emptyView = view.findViewById(R.id.emptyListTV)
                    emptyView.visibility = View.GONE
                    skillsRV = view.findViewById(R.id.SkillsList)
                    skillsRV.layoutManager = LinearLayoutManager(this.context)
                    Log.d("Skills", skills.keys.toString())
                    val ss : MutableList<String> = skills.keys.toMutableList()
                    skillsRV.adapter = SkillsAdapter(ss)
                }
            }
        }
    }
}