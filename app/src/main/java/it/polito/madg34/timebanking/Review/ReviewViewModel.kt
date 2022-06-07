package it.polito.madg34.timebanking.Review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import it.polito.madg34.timebanking.FirestoreRepository

class ReviewViewModel : ViewModel() {

    val currentUserReviews : MutableLiveData<List<Review>> = MutableLiveData<List<Review>>().also { loadCurrentReviews() }
    val otherUserReviews : MutableLiveData<List<Review>> = MutableLiveData<List<Review>>()

    var type = 0 // 0 as a requester , 1 is a offerer

    private var listener1: ListenerRegistration? = null

    private fun loadCurrentReviews() {
        listener1 = FirestoreRepository().getReviews(FirestoreRepository.currentUser.email!!).addSnapshotListener(
            EventListener{ value , e ->
            if(e != null){
                currentUserReviews.value = mutableListOf()
                return@EventListener
            }
                currentUserReviews.value = value?.toObjects(Review::class.java)
        })
    }

    fun getCurretUserReviews() : LiveData<List<Review>>{
        return currentUserReviews
    }

    fun loadOtherReviews(email: String): Task<QuerySnapshot> {
        return FirestoreRepository().getReviews(email).get().addOnSuccessListener {
            if(it != null){
                otherUserReviews.value = it.toObjects(Review::class.java)
            }
        }
    }

    fun saveReview(review : Review): Task<Void> {
        return FirestoreRepository().saveNewReview(review)
    }

}