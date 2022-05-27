package it.polito.madg34.timebanking.chat

import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@Keep
@IgnoreExtraProperties
data class Chat(
    @get: PropertyName("FromUser") @set: PropertyName("FromUser") var fromUser: Map<String,Map<String,String>>?,
    )