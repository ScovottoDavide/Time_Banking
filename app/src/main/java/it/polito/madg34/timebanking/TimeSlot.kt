package it.polito.madg34.timebanking

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
data class TimeSlot(
    @get: PropertyName("TITLE") @set: PropertyName("TITLE") var title: String = "",
    @get: PropertyName("DESCRIPTION") @set: PropertyName("DESCRIPTION") var description: String = "",
    @get: PropertyName("DATE") @set: PropertyName("DATE") var date: String = "",
    @get: PropertyName("TIME") @set: PropertyName("TIME") var time: String = "",
    @get: PropertyName("DURATION") @set: PropertyName("DURATION") var duration: String = "",
    @get: PropertyName("LOCATION") @set: PropertyName("LOCATION") var location: String = "",
    @get: PropertyName("PUBLISHED_BY") @set: PropertyName("PUBLISHED_BY") var published_by: String = "",
    @get: PropertyName("RELATED_SKILL") @set: PropertyName("RELATED_SKILL") var related_skill: String = "",
    @get: PropertyName("INDEX") @set: PropertyName("INDEX") var index: Int
)

fun emptyTimeSlot(): TimeSlot {
    return TimeSlot("", "", "", "", "", "", "", "", -1)
}
