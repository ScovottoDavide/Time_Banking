package it.polito.madg34.timebanking.Review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.madg34.timebanking.FirestoreRepository
import it.polito.madg34.timebanking.Profile.ProfileViewModel
import it.polito.madg34.timebanking.R

class ReviewFragment : Fragment() {

    val vmProfile: ProfileViewModel by activityViewModels()
    val vmReview : ReviewViewModel by activityViewModels()

    lateinit var reviewList : List<Review>
    lateinit var reviewRV: RecyclerView

    lateinit var pageTitle : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.review_fragment_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pageTitle = view.findViewById(R.id.chatTitle)
        vmProfile.clickedEmail.observe(viewLifecycleOwner){ email ->
            if(email == FirestoreRepository.currentUser.email){
                vmReview.getCurretUserReviews().observe(viewLifecycleOwner){
                    if(it != null){
                        reviewList = it.filter { x -> x.type == vmReview.type }.sortedByDescending { it.timestamp }
                        reviewRV = view.findViewById(R.id.ReviewList)
                        reviewRV.layoutManager = LinearLayoutManager(this.context)
                        reviewRV.adapter = ReviewAdapter(reviewList)
                        if(vmReview.type == 1)
                            pageTitle.text = "Reviews as Requester"
                        else
                            pageTitle.text = "Reviews as Offerer"
                    }
                }
            }else{
                vmReview.loadOtherReviews(vmProfile.clickedEmail.value!!).addOnSuccessListener {
                    reviewList = it.toObjects(Review::class.java).filter { x -> x.type == vmReview.type }.sortedByDescending { it.timestamp }
                    reviewRV = view.findViewById(R.id.ReviewList)
                    reviewRV.layoutManager = LinearLayoutManager(this.context)
                    reviewRV.adapter = ReviewAdapter(reviewList)
                    if(vmReview.type == 1)
                        pageTitle.text = "Reviews as Requester"
                    else
                        pageTitle.text = "Reviews as Offerer"
                }
            }
        }
    }
}