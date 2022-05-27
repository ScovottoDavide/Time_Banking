package it.polito.madg34.timebanking.Messages

import com.google.firebase.firestore.PropertyName

class Message (
    @get: PropertyName("MESSAGE_CONTENT") @set: PropertyName("MESSAGE_CONTENT") var messageContent: String,
    @get: PropertyName("READ") @set: PropertyName("READ") var read: Int,
    @get: PropertyName("SENT_BY") @set: PropertyName("SENT_BY") var sentBy: String,
    @get: PropertyName("TIMESTAMP") @set: PropertyName("TIMESTAMP") var timeStamp: Long
    )

fun emptyMessage() : Message{
    return Message("", 0, "", 0)
}