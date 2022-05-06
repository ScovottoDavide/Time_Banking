package it.polito.madg34.timebanking

class TimeSlot(var title : String,
                    var description : String,
                    var date : String,
                    var time : String,
                    var duration : String,
                    var location : String,
                    )

fun emptyTimeSlot() : TimeSlot{
    return TimeSlot("", "", "", "","", "")
}
