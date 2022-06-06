package it.polito.madg34.timebanking.Review

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
data class Review(
    @get: PropertyName("COMMENT") @set: PropertyName("COMMENT") var comment: String? = "",
    @get: PropertyName("ID_ADV") @set: PropertyName("ID_ADV") var id_adv: String? = "",
    @get: PropertyName("REVIEWEE") @set: PropertyName("REVIEWEE") var reviewee: String? = "",
    @get: PropertyName("REVIEWER") @set: PropertyName("REVIEWER") var reviewer: String?  = "",
    @get: PropertyName("TIMESTAMP") @set: PropertyName("TIMESTAMP") var timestamp: Long? = 0,
    @get: PropertyName("SCORE") @set: PropertyName("SCORE") var score: Float = 0f,
    @get: PropertyName("TYPE") @set: PropertyName("TYPE") var type: Int = 0, // 0 as a requester , 1 is a offerer
    )