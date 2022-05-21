package it.polito.madg34.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
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

    var advs : MutableList<Skills> = mutableListOf()
    var localSkills : MutableList<String> = mutableListOf()

    lateinit var  optionButton : ImageButton

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.skillsfragment_layout, container, false)
        setHasOptionsMenu(true)
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
                    localSkills = skills.keys.toMutableList()
                    advs = skills.values.toMutableList()
                    skillsRV.adapter = SkillsAdapter(localSkills,advs)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.homepage_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                val popupMenu = PopupMenu(context, view?.findViewById(R.id.anchor))
                popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    if (item != null) {
                        when (item.itemId) {
                            R.id.sort -> {
                                sortByName()
                                skillsRV.adapter?.notifyDataSetChanged()
                                true
                            }
                            else -> super.onOptionsItemSelected(item)
                        }
                    } else {
                        false
                    }
                })
                popupMenu.show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun sortByName() {
        localSkills.sortWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it }
        )
    }
}