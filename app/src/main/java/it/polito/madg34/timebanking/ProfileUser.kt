package it.polito.madg34.timebanking

import android.net.Uri

class ProfileUser (
     var img : Uri?,
     var fullName : String?,
     var nickname : String?,
     var email : String?,
     var location : String?,
     var aboutUser : String?,
     var skills: MutableMap<String, String>?
     )