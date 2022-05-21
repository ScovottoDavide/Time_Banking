package it.polito.madg34.timebanking

import android.util.Log
import android.view.View
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

class ProfileViewModel : ViewModel() {

    var showed = false
    var needRegistration = false
    var nicknameOk = false

    var localProfile: ProfileUser? = ProfileUser()
    var profileToShow : ProfileUser? = ProfileUser()
    var currentPhotoPath = ""
    var clickedEmail : MutableLiveData<String> = MutableLiveData("")

    val profile: MutableLiveData<ProfileUser> by lazy { MutableLiveData(ProfileUser()).also { loadProfile() } }
    val viewProfile: MutableLiveData<ProfileUser> by lazy { MutableLiveData(ProfileUser()).also { loadViewProfile() } }

    var listenerNavigation: View.OnClickListener? = null
    private var listener1: ListenerRegistration? = null

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

    fun loadViewProfile() : Task<DocumentSnapshot> {
        return FirestoreRepository().getViewUser(clickedEmail.value!!).get().addOnSuccessListener {
            if(it != null){
                viewProfile.value = it.toObject(ProfileUser::class.java)
                profileToShow = it.toObject(ProfileUser::class.java)
                Log.d("carica", viewProfile.value.toString())
            }
        }
    }

    fun getDBUser(): LiveData<ProfileUser> {
        return profile
    }

    fun modifyUserProfile(value: ProfileUser): Task<Void> {
        return FirestoreRepository().setUser(value)
    }

    fun getViewProfile(): LiveData<ProfileUser> {
        return viewProfile
    }

    fun checkNicknameVM(registrationNickname : String): Task<QuerySnapshot> {
        return FirestoreRepository().checkNickname(registrationNickname).get().addOnSuccessListener {
            nicknameOk = it.isEmpty
        }
    }

}
