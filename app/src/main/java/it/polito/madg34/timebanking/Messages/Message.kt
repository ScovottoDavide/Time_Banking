package it.polito.madg34.timebanking.Messages

import com.google.firebase.firestore.PropertyName

class Message (
    @get: PropertyName("ID") @set: PropertyName("ID") var id: String = "",
    @get: PropertyName("MESSAGE_CONTENT") @set: PropertyName("MESSAGE_CONTENT") var messageContent: String = "",
    @get: PropertyName("READ") @set: PropertyName("READ") var read: Int = 0,
    @get: PropertyName("SENT_BY") @set: PropertyName("SENT_BY") var sentBy: String = "",
    @get: PropertyName("RECEIVED_BY") @set: PropertyName("RECEIVED_BY") var receivedBy: String = "",
    @get: PropertyName("TIMESTAMP") @set: PropertyName("TIMESTAMP") var timeStamp: Long = 0,
    @get: PropertyName("RELATED_ADV") @set: PropertyName("RELATED_ADV") var relatedAdv: String = ""
    )

fun emptyMessage() : Message{
    return Message("", "", 0, "", "", 0, "")
}