package it.polito.madg34.timebanking

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
data class Skills (
    @get: PropertyName("RELATED_ADVS") @set: PropertyName("RELATED_ADVS") var relatedAdvs: String = ""
)