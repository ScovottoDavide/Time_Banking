package it.polito.madg34.timebanking

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private var fireStoreDB = FirebaseFirestore.getInstance()

    companion object{
        // Current Authenticated user
        lateinit var currentUser: FirebaseUser
    }

    /*
        Function to get the document of the current user
    */
    fun getUser(): DocumentReference {
        return fireStoreDB.collection("users").document(currentUser.email!!)
    }

    /*
        Function to set the document of the current user
    */
    fun setUser(value : ProfileUser): Task<Void> {
        return fireStoreDB.collection("users").document(currentUser.email!!).set(value)
    }

}