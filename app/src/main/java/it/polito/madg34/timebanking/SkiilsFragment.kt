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
                    val localSkills : MutableList<String> = skills.keys.toMutableList()
                    val advs : MutableList<Skills> = skills.values.toMutableList()
                    skillsRV.adapter = SkillsAdapter(localSkills,advs)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.homepage_menu, menu)
        //optionButton = menu.findItem(R.id.filter).actionView as ImageButton
        //optionButton.setImageDrawable(resources.getDrawable(R.drawable.filter_variant))
        //optionButton.setBackgroundColor(resources.getColor(R.color.Goldenrod))
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                Toast.makeText(requireContext(), "ciao", Toast.LENGTH_SHORT).show()
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)
                popupMenu.show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}