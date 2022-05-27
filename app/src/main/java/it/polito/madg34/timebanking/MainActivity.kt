package it.polito.madg34.timebanking

import android.content.Intent
import android.location.GnssAntennaInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
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
    val vmSkills: SkillsViewModel by viewModels()
    var profile: ProfileUser = emptyProfile()
    var timeSlots: List<TimeSlot> = emptyList()
    lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView

    private var isPopupOpenDiscard = false
    private var isPopupOpenLogOut = false
    lateinit var alertDialog: AlertDialog
    lateinit var alertDialogLogOut: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isPopupOpenDiscard = savedInstanceState?.getBoolean("shouldShowPopup") == true
        isPopupOpenLogOut = savedInstanceState?.getBoolean("shouldShowPopupLogOut") == true
        if (isPopupOpenDiscard) {
            discardChanges()
        }
        if (isPopupOpenLogOut) {
            logOut()
        }
        // Get the flag from the intent extra in order to know if the first registration is needed
        vmProfile.needRegistration = intent.getBooleanExtra("INTENT_NEED_REGISTRATION_EXTRA", false)

        drawerLayout = findViewById(R.id.drawer_layout)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.timeSlotListFragment, R.id.showProfileFragment, R.id.skillsFragment, R.id.chatFragment),
            drawerLayout
        )

        val toolbar: MaterialToolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        toolbar.setNavigationOnClickListener {
            if (navController.currentDestination?.id == navController.graph[R.id.timeSlotDetailsFragment].id)
                onBackPressed()
            else if (navController.currentDestination?.id == navController.graph[R.id.editSkillFragment].id
                || navController.currentDestination?.id == navController.graph[R.id.editProfileFragment].id
                || navController.currentDestination?.id == navController.graph[R.id.addSkillFragment].id
                || navController.currentDestination?.id == navController.graph[R.id.timeSlotEditFragment].id
            ) {
                discardChanges()
            } else if (navController.currentDestination?.id == navController.graph[R.id.timeSlotListFragment].id
                && vmSkills.fromHome.value!!
            ) {
                navController.navigate(R.id.action_timeSlotListFragment_to_skillsFragment)
            } else if (navController.currentDestination?.id == navController.graph[R.id.showProfileFragment].id
                && vmProfile.clickedEmail.value != FirestoreRepository.currentUser.email
            ) {
                vmProfile.clickedEmail.value = FirestoreRepository.currentUser.email.toString()
                vmProfile.profile.value = vmProfile.profile.value
                this.onBackPressed()
            } else if (navController.currentDestination?.id == navController.graph[R.id.skillsFragment].id) {
                loadNavigationHeader()
                drawerLayout.openDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        vmProfile.getDBUser().observe(this) {
            if (it == null && vmProfile.needRegistration) {
                navController.navigate(R.id.editProfileFragment)
            } else if (it == null)
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
                        vmProfile.clickedEmail.value =
                            FirestoreRepository.currentUser.email.toString()
                        navController.navigate(R.id.showProfileFragment)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_listServices -> {
                    if (navController.currentDestination?.id != navController.graph[R.id.timeSlotListFragment].id && !vmSkills.fromHome.value!!) {
                        navController.navigate(R.id.timeSlotListFragment)
                    } else if (navController.currentDestination?.id == navController.graph[R.id.timeSlotListFragment].id && vmSkills.fromHome.value!!) {
                        vmSkills.fromHome.value = false
                        navController.navigate(R.id.timeSlotListFragment)
                    } else if (navController.currentDestination?.id == navController.graph[R.id.showProfileFragment].id && vmSkills.fromHome.value!!) {
                        vmSkills.fromHome.value = false
                        navController.navigate(R.id.timeSlotListFragment)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_listSkills -> {
                    if (navController.currentDestination?.id != navController.graph[R.id.skillsFragment].id) {
                        vmSkills.loadAllSkills()
                        vmSkills.currentSkillAdvs.value = vmSkills.currentSkillAdvs.value
                        navController.navigate(R.id.skillsFragment)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.sentReqs -> {
                    if (navController.currentDestination?.id != navController.graph[R.id.chatFragment].id) {
                        navController.navigate(R.id.chatFragment)
                    }
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.timeSlotListFragment && vmSkills.fromHome.value!!) {
                supportActionBar?.setHomeAsUpIndicator(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else if (destination.id == R.id.showProfileFragment && vmProfile.clickedEmail.value != FirestoreRepository.currentUser.email) {
                supportActionBar?.setHomeAsUpIndicator(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else if (destination.id == R.id.timeSlotEditFragment || destination.id == R.id.timeSlotDetailsFragment
                || destination.id == R.id.editProfileFragment || destination.id == R.id.editSkillFragment
                || destination.id == R.id.addSkillFragment
            )
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            else
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        val logoutButton = findViewById<Button>(R.id.logout_btn)
        logoutButton.setOnClickListener {
            logOut()
        }

        vmTimeSlot.currentUserAdvs.observe(this) {
            if (vmTimeSlot.currentUserAdvs.value?.size == 0 && navController.currentDestination?.id == navController.graph[R.id.timeSlotListFragment].id) {
                // Dirty way, trigger reacreation of fragment to show empty message
                navController.navigate(R.id.timeSlotListFragment)
            }
        }

        vmSkills.getAllSkillsVM().observe(this) {
            Log.d("HOME", it.toString())
            if (it.isEmpty() && navController.currentDestination?.id == navController.graph[R.id.skillsFragment].id) {
                // Dirty way, trigger reacreation of fragment to show empty message
                navController.navigate(R.id.skillsFragment)
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
            Glide.with(this).load(profile.img).into(imgProfile)
        } else imgProfile.setImageResource(R.drawable.user)
    }

    /*
        Function to perform the logout and to return to the Auth Activity
    */
    private fun logOut() {
        isPopupOpenLogOut = true
        alertDialogLogOut = AlertDialog.Builder(this)
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
                isPopupOpenLogOut = false
            }
            .setNegativeButton("No") { _, _ ->
                isPopupOpenLogOut = false
            }
            .show()

        alertDialogLogOut.setOnDismissListener { isPopupOpenLogOut = false }
    }

    private fun discardChanges() {
        isPopupOpenDiscard = true
        alertDialog = AlertDialog.Builder(this)
            .setTitle("Warning!")
            .setMessage("Do you want to discard the changes?")
            .setPositiveButton("Yes") { _, _ ->
                if (navController.currentDestination?.id == navController.graph[R.id.editSkillFragment].id)
                    navController.navigate(R.id.action_editSkillFragment_to_editProfileFragment)
                else if (navController.currentDestination?.id == navController.graph[R.id.addSkillFragment].id)
                    navController.navigate(R.id.action_addSkillFragment_to_editProfileFragment)
                else if (navController.currentDestination?.id == navController.graph[R.id.editProfileFragment].id)
                    navController.navigate(R.id.action_editProfileFragment_to_showProfileFragment)
                else if (navController.currentDestination?.id == navController.graph[R.id.timeSlotEditFragment].id) {
                    navController.navigate(R.id.action_timeSlotEditFragment_to_timeSlotListFragment)
                }
                isPopupOpenDiscard = false
            }
            .setNegativeButton("No") { _, _ ->
                isPopupOpenDiscard = false
            }
            .show()

        alertDialog.setOnDismissListener { isPopupOpenDiscard = false }
    }

    override fun onPause() {
        super.onPause()
        if (isPopupOpenDiscard)
            alertDialog.dismiss()
        if (isPopupOpenLogOut)
            alertDialogLogOut.dismiss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("shouldShowPopup", isPopupOpenDiscard)
        outState.putBoolean("shouldShowPopupLogOut", isPopupOpenLogOut)
    }
}

