package it.polito.madg34.timebanking.HomeSkills

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import it.polito.madg34.timebanking.R
import it.polito.madg34.timebanking.TimeSlots.TimeSlotViewModel

class SkillsFragment : Fragment() {

    val vmSkills: SkillsViewModel by activityViewModels()

    private var skills: MutableMap<String, Skills> = mutableMapOf()
    lateinit var skillsRV: RecyclerView

    lateinit var emptyView: TextInputLayout
    lateinit var homepageLogo: ImageView
    lateinit var homepageAppName: TextView

    var advs: MutableList<Skills> = mutableListOf()
    var localSkills: MutableList<String> = mutableListOf()


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

        emptyView = view.findViewById(R.id.emptyListTV)
        homepageLogo = view.findViewById(R.id.homepageLogo)
        homepageAppName = view.findViewById(R.id.homepageAppName)

        vmSkills.getAllSkillsVM().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                skills = it
                if (skills.isEmpty()) {
                    emptyView.visibility = View.VISIBLE
                    homepageAppName.visibility = View.VISIBLE
                    homepageLogo.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.GONE
                    homepageAppName.visibility = View.GONE
                    homepageLogo.visibility = View.GONE
                    skillsRV = view.findViewById(R.id.SkillsList)
                    skillsRV.layoutManager = LinearLayoutManager(this.context)
                    vmSkills.filtered.observe(viewLifecycleOwner) {
                        if (!it) {
                            localSkills = skills.keys.toMutableList()
                            advs = skills.values.toMutableList()
                            skillsRV.adapter = SkillsAdapter(localSkills, advs)
                        } else {
                            skillsRV.adapter =
                                SkillsAdapter(vmSkills.filteredSkills, vmSkills.filteredAdvs)
                        }
                    }
                }
            } else {
                emptyView.visibility = View.VISIBLE
                homepageAppName.visibility = View.VISIBLE
                homepageLogo.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (skills.isNotEmpty()) {
            inflater.inflate(R.menu.homepage_menu, menu)
            // Associate searchable configuration with the SearchView
            val searchManager =
                requireContext().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val search = menu.findItem(R.id.search).actionView as SearchView
            search.isIconifiedByDefault = false
            search.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return true
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    val local = skills
                    val together = local.map { it.key + ":" + it.value.relatedAdvs }
                    //gets the filtered list based on user entered text in search box
                    val filteredList = newText?.let { filter(together, it) }
                    localSkills = mutableListOf()
                    advs = mutableListOf()
                    filteredList?.forEach {
                        val splitted = it.split(":")
                        localSkills.add(splitted[0])
                        advs.add(Skills(splitted[1]))
                    }
                    skillsRV.adapter = SkillsAdapter(localSkills, advs)
                    skillsRV.adapter?.notifyDataSetChanged()
                    return true
                }

            })
            return super.onCreateOptionsMenu(menu, inflater)
        }
    }

    // filters the existing list that's provided to the List Adapter
    private fun filter(mList: List<String>, newText: String): List<String>? {
        val filteredList: MutableList<String> = ArrayList()
        for (item in mList) {
            if (item.split(":")[0].lowercase().contains(newText.lowercase())) {
                filteredList.add(item)
            }
        }
        return filteredList
    }

    @SuppressLint("NotifyDataSetChanged", "ResourceAsColor")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter -> {
                val popupMenu = PopupMenu(context, view?.findViewById(R.id.anchor))
                popupMenu.menuInflater.inflate(R.menu.filter_menu, popupMenu.menu)
                watchFilterMenu(popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    if (item != null) {
                        when (item.itemId) {
                            R.id.sort -> {
                                vmSkills.selection.value = item.itemId
                                sortByName()
                                vmSkills.filtered.value = true
                                skillsRV.adapter?.notifyDataSetChanged()
                                true
                            }
                            R.id.popularity -> {
                                vmSkills.selection.value = item.itemId
                                sortByPopularity()
                                vmSkills.filtered.value = true
                                skillsRV.adapter =
                                    SkillsAdapter(vmSkills.filteredSkills, vmSkills.filteredAdvs)
                                skillsRV.adapter?.notifyDataSetChanged()
                                true
                            }
                            R.id.nothing -> {
                                vmSkills.selection.value = item.itemId
                                vmSkills.filtered.value = false
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
        val local = skills
        val together = local.map {
            it.key + ":" + it.value.relatedAdvs
        }
        val sorted =
            together.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.split(":")[0] })
        localSkills = mutableListOf()
        advs = mutableListOf()
        sorted.forEach {
            val splitted = it.split(":")
            localSkills.add(splitted[0])
            advs.add(Skills(splitted[1]))
        }
        vmSkills.filteredSkills = localSkills
        vmSkills.filteredAdvs = advs

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortByPopularity() {
        val local = skills
        val together = local.map {
            it.key + ":" + it.value.relatedAdvs
        }
        val sorted = together.sortedByDescending { it.length }
        localSkills = mutableListOf()
        advs = mutableListOf()
        sorted.forEach {
            val splitted = it.split(":")
            localSkills.add(splitted[0])
            advs.add(Skills(splitted[1]))
        }
        vmSkills.filteredSkills = localSkills
        vmSkills.filteredAdvs = advs
    }

    private fun watchFilterMenu(menu : Menu){
        vmSkills.selection.observe(viewLifecycleOwner){
            when(it){
                R.id.sort -> {
                    val menuItem = menu.findItem(R.id.sort)
                    menuItem.isChecked = true
                }
                R.id.popularity -> {
                    val menuItem = menu.findItem(R.id.popularity)
                    menuItem.isChecked = true
                }
                R.id.nothing -> {
                    val menuItem = menu.findItem(R.id.nothing)
                    menuItem.isChecked = true
                }
            }
        }
    }
}