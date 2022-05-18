package it.polito.madg34.timebanking

import android.content.Intent
import android.location.GnssAntennaInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
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
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView


class MainActivity : AppCompatActivity() {
    val vmProfile: ProfileViewModel by viewModels()
    val vmTimeSlot: TimeSlotViewModel by viewModels()
    var profile: ProfileUser = emptyProfile()
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Get the flag from the intent extra in order to know if the first registration is needed
        vmProfile.needRegistration = intent.getBooleanExtra("INTENT_NEED_REGISTRATION_EXTRA", false)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.timeSlotListFragment, R.id.showProfileFragment),
            drawerLayout
        )

        val toolbar: MaterialToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)


        vmProfile.getDBUser().observe(this) {
            if (it == null && vmProfile.needRegistration){
                //vmProfile.listenerNavigation = View.OnClickListener { logOut() }
                //toolbar.setNavigationOnClickListener { vmProfile.listenerNavigation }
                navController.navigate(R.id.editProfileFragment)
            }
            else if (it == null)
                Toast.makeText(this, "Firebase failure", Toast.LENGTH_SHORT).show()
            else {
                profile = it
                loadNavigationHeader()
            }
        }

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

        val logoutButton = findViewById<Button>(R.id.logout_btn)
        logoutButton.setOnClickListener {
            logOut()
        }

        vmTimeSlot.listServices.observe(this) {
            if (vmTimeSlot.listServices.value?.size == 0 && navController.currentDestination?.id == navController.graph.startDestinationId) {
                // Dirty way, trigger reacreation of fragment to show empty message
                navController.navigate(R.id.timeSlotListFragment)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun loadNavigationHeader() {
        val header = navView.getHeaderView(0)
        val name = header?.findViewById<TextView>(R.id.nomecognome)
        val email = header?.findViewById<TextView>(R.id.headerMail)
        val imgProfile = header.findViewById<CircleImageView>(R.id.nav_header_userImg)
        name?.text = profile.fullName
        email?.text = profile.email
        if (!profile.img.isNullOrEmpty()) {
            //imgProfile.setImageURI(Uri.parse(profile.img))
            Glide.with(this).load(profile.img).into(imgProfile)
        } else imgProfile.setImageResource(R.drawable.user)
    }

    /*
        Function to perform the logout and to return to the Auth Activity
    */
    private fun logOut() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Log out")
            .setMessage("Do you want to log out from the Time Earn app?")
            .setPositiveButton("Yes") { _, _ ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.web_client_id))
                    .requestEmail()
                    .build()

                // Sign out from Google
                GoogleSignIn.getClient(this, gso).signOut()
                    .addOnCompleteListener(this) {
                        if (it.isSuccessful) {
                            // Sign out from Firebase
                            Firebase.auth.signOut()
                            Toast.makeText(this, "Successfully logged out!", Toast.LENGTH_SHORT)
                                .show()
                            startActivity(Intent(this, AuthActivity::class.java))
                            finish()
                        }
                    }
            }
            .setNegativeButton("No") { _, _ ->
            }
            .show()
    }
}

