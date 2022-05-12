package it.polito.madg34.timebanking

import android.net.Uri
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
data class ProfileUser (
     @get: PropertyName("uri") @set: PropertyName("uri") var img : String? = "",
     @get: PropertyName("FULLNAME") @set: PropertyName("FULLNAME") var fullName : String? = "" ,
     @get: PropertyName("NICKNAME") @set: PropertyName("NICKNAME") var nickname : String? = "",
     @get: PropertyName("EMAIL") @set: PropertyName("EMAIL") var email : String?  = "",
     @get: PropertyName("LOCATION") @set: PropertyName("LOCATION") var location : String? = "",
     @get: PropertyName("ABOUT_ME") @set: PropertyName("ABOUT_ME") var aboutUser : String? = "" ,
     @get: PropertyName("Skills") @set: PropertyName("Skills") var skills: MutableMap<String, String>? = mutableMapOf()
     )

fun emptyProfile() : ProfileUser {
     return  ProfileUser("android.resource://it.polito.madg34.timebanking/"+R.drawable.user_icon, "", "", "", "", "", mutableMapOf())
}