package it.polito.madg34.timebanking

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.get
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView : NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment  = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController;
        appBarConfiguration = AppBarConfiguration(setOf(R.id.timeSlotListFragment, R.id.showProfileFragment), drawerLayout)

        val toolbar: MaterialToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        navView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener{ item ->
            when(item.itemId){
                R.id.nav_profile -> {
                    if(navController.currentDestination?.id != navController.graph[R.id.showProfileFragment].id)
                        navController.navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_listServices -> {
                    if(navController.currentDestination?.id != navController.graph.startDestinationId)
                        navController.navigate(R.id.action_showProfileFragment_to_timeSlotListFragment)
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun showHamburgerIcon() {
        val toolbar = findViewById<MaterialToolbar>(R.id.my_toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0,0)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        toggle.isDrawerIndicatorEnabled = true
    }
}