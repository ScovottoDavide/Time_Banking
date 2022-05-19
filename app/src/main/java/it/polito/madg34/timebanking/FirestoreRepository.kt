package it.polito.madg34.timebanking

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*

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
       Function to get the documents of all users
   */
    fun getOthersUser(): CollectionReference {
        return fireStoreDB.collection("users")
    }

    /*
        Function to set the document of the current user
    */
    fun setUser(value : ProfileUser): Task<Void> {
        return fireStoreDB.collection("users").document(currentUser.email!!).set(value)
    }

    fun setSkills(value : String) {
        // get all advs from its collection to know if there are advs (else empty list)
        fireStoreDB.collection("skills").document(value).set("lista di advs")
    }

    fun getAdvs(): Query{
        return fireStoreDB.collection("advertisements").whereEqualTo("PUBLISHED_BY", currentUser.email!!)
    }

    fun getAllAdvs(): CollectionReference{
        return fireStoreDB.collection("advertisements")
    }

    fun saveAdvDB(value : TimeSlot): Task<Void> {
        return fireStoreDB.collection("advertisements").document(value.id).set(value)
    }

    fun updateAdvDB(value : TimeSlot) : Task<Void>{
        return fireStoreDB.collection("advertisements").document(value.id).set(value)
    }

    fun removeAdvDB(value : TimeSlot) : Task<Void> {
        return fireStoreDB.collection("advertisements").document(value.id).delete()
    }



}