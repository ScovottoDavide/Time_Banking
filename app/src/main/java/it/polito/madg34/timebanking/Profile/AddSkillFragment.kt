package it.polito.madg34.timebanking.Profile

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import it.polito.madg34.timebanking.R

class AddSkillFragment: Fragment() {
    val vm : ProfileViewModel by activityViewModels()

    lateinit var skillName : EditText
    lateinit var skillDesc : EditText
    lateinit var item : ProfileUser
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.addskillfragment_layout, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item  = vm.localProfile!!

        skillName = view.findViewById(R.id.skillName)
        skillDesc = view.findViewById(R.id.skillDescription)

        skillName.hint = "New skill name ..."
        skillDesc.hint = "New skill description ..."

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(skillName.text.isNotEmpty()){
                        val t = skillName.text.mapIndexed { index, c ->
                            if (index==0) {
                                c.uppercaseChar()
                            }
                            else {
                                c.lowercaseChar()
                            }
                        }
                        var sNew = t.joinToString("")
                        if(sNew.last() == ' ')
                            sNew = sNew.dropLast(1)
                        if(skillDesc.text.isEmpty())
                            item?.skills?.set(sNew, "No description")
                        else
                            item?.skills?.set(sNew, skillDesc.text.toString())
                        Snackbar.make(view, "Skill successfully created!", Snackbar.LENGTH_LONG).show()
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    } else
                        Snackbar.make(view, "Skill name cannot be empty!!", Snackbar.LENGTH_LONG).show()
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save_page, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save -> {
                closeKeyboard()
                requireActivity().onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun closeKeyboard() {
        // this will give us the view which is currently focus in this layout
        val v: View? = this.view?.findFocus()

        val manager: InputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(v?.windowToken, 0)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        closeKeyboard()
        updateSKill()
    }

    private fun updateSKill() {
        if (skillName.text.isNotEmpty()) {
            val t = skillName.text.mapIndexed { index, c ->
                if (index == 0) {
                    c.uppercaseChar()
                } else {
                    c.lowercaseChar()
                }
            }
            var sNew = t.joinToString("")
            if (sNew.last() == ' ')
                sNew = sNew.dropLast(1)
            if (skillDesc.text.isEmpty())
                item?.skills?.set(sNew, "No description")
            else
                item?.skills?.set(sNew, skillDesc.text.toString())
        }
    }
}