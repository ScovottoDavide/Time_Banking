package it.polito.madg34.timebanking

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.get
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    lateinit var vmProfile: ProfileViewModel
    lateinit var vmTimeSlot: TimeSlotViewModel
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController;
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.timeSlotListFragment, R.id.showProfileFragment),
            drawerLayout
        )

        val toolbar: MaterialToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    if (navController.currentDestination?.id != navController.graph[R.id.showProfileFragment].id) {
                        navController.navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_listServices -> {
                    if (navController.currentDestination?.id != navController.graph.startDestinationId)
                        navController.navigate(R.id.action_showProfileFragment_to_timeSlotListFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
        vmProfile = ViewModelProvider(this).get()

        vmProfile.profile.observe(this){
            if (vmProfile.profile.value != null) {
                val header = navView.getHeaderView(0)
                val name = header?.findViewById<TextView>(R.id.nomecognome)
                if (!vmProfile.profile.value?.fullName?.isEmpty()!!)
                    name?.text = vmProfile.profile.value?.fullName
                val email = header?.findViewById<TextView>(R.id.headerMail)
                if (!vmProfile.profile.value?.email?.isEmpty()!!)
                    email?.text = vmProfile.profile.value?.email
                val imgProfile = header.findViewById<CircleImageView>(R.id.nav_header_userImg)
                if (vmProfile.profile.value?.img != null) {
                    imgProfile.setImageURI(Uri.parse(vmProfile.profile.value?.img))
                }
            }
        }

       vmTimeSlot = ViewModelProvider(this).get()
        vmTimeSlot.listServices.observe(this){
            if(vmTimeSlot.listServices.value?.size == 0 && navController.currentDestination?.id == navController.graph.startDestinationId){
                // Dirty way, trigger reacreation of fragment to show empty message
                navController.navigate(R.id.timeSlotListFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}