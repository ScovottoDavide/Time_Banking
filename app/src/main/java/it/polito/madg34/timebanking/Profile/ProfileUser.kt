package it.polito.madg34.timebanking.Profile

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
data class ProfileUser(
    @get: PropertyName("uri") @set: PropertyName("uri") var img: String? = "",
    @get: PropertyName("FULLNAME") @set: PropertyName("FULLNAME") var fullName: String? = "",
    @get: PropertyName("NICKNAME") @set: PropertyName("NICKNAME") var nickname: String? = "",
    @get: PropertyName("EMAIL") @set: PropertyName("EMAIL") var email: String?  = "",
    @get: PropertyName("LOCATION") @set: PropertyName("LOCATION") var location: String? = "",
    @get: PropertyName("ABOUT_ME") @set: PropertyName("ABOUT_ME") var aboutUser: String? = "",
    @get: PropertyName("Skills") @set: PropertyName("Skills") var skills: MutableMap<String, String> = mutableMapOf(),
    @get: PropertyName("TOTAL_TIME") @set: PropertyName("TOTAL_TIME") var total_time: String? = "0h:0m",
    @get: PropertyName("OFFERER_SCORE") @set: PropertyName("OFFERER_SCORE") var offererScore: Float? = 0f,
    @get: PropertyName("OFFERER_NUMBER") @set: PropertyName("OFFERER_NUMBER") var offererNumber: Int? = 0,
    @get: PropertyName("REQUESTER_SCORE") @set: PropertyName("REQUESTER_SCORE") var requesterScore: Float? = 0f,
    @get: PropertyName("REQUESTER_NUMBER") @set: PropertyName("REQUESTER_NUMBER") var requesterNumber: Int? = 0

    )

fun emptyProfile() : ProfileUser {
     return  ProfileUser(null, "", "", "", "", "", mutableMapOf(), "0h:0m",0f,0,0f,0)
}