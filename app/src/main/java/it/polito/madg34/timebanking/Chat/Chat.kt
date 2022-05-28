package it.polito.madg34.timebanking.Chat

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
class Chat(
    @get: PropertyName("INFO") @set: PropertyName("INFO") var info: String = "",
    ){
    fun equals(c1: Chat, c2 : Chat): Boolean {
        return c1.info == c2.info
    }
}
