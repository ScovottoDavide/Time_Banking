package it.polito.madg34.timebanking

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import java.lang.Exception

class ProfileViewModel : ViewModel() {

    var showed = false
    var needRegistration = false

    var localProfile: ProfileUser? = ProfileUser()
    val localSkills: MutableList<String> = mutableListOf()
    var currentPhotoPath = ""

    val allProfiles: MutableLiveData<List<ProfileUser>> by lazy { MutableLiveData<List<ProfileUser>>().also { getAllUsersFromDb() } }
    val profile: MutableLiveData<ProfileUser> by lazy { MutableLiveData(ProfileUser()).also { loadProfile() } }

    var listenerNavigation: View.OnClickListener? = null
    private var listener1: ListenerRegistration? = null
    private var listener2: ListenerRegistration? = null

    /*
    * Load Profile current user
    * */
    private fun loadProfile() {
        listener1 = FirestoreRepository().getUser().addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                profile.value = null
                return@EventListener
            }
            profile.value = value?.toObject(ProfileUser::class.java)
        })
    }

    /*
    * Load all Profiles to get all the skills
    * */
    private fun getAllUsersFromDb() {
        listener2 =
            FirestoreRepository().getOthersUser().addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    profile.value = null
                    return@EventListener
                }
                allProfiles.value = value!!.mapNotNull { d ->
                    d.toProfileUser()
                }
            })
    }

    fun getDBUser(): LiveData<ProfileUser> {
        return profile
    }

    fun modifyUserProfile(value: ProfileUser): Task<Void> {
        return FirestoreRepository().setUser(value)
    }

    fun getAllUsers(): LiveData<List<ProfileUser>> {
        return allProfiles
    }

}

private fun DocumentSnapshot.toProfileUser(): ProfileUser {
    return try {
        val fullName = get("FULLNAME") as String
        val nickname = get("NICKNAME") as String
        val email = get("EMAIL") as String
        val location = get("LOCATION") as String
        val useDesc = get("ABOUT_ME") as String
        val img = get("uri") as String
        val skills = get("Skills") as MutableMap<String, String>
        ProfileUser(img, fullName, nickname, email, location, useDesc, skills)


    } catch (e: Exception) {
        e.printStackTrace()
        emptyProfile()
    }

}
