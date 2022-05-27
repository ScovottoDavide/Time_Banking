package it.polito.madg34.timebanking

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import it.polito.madg34.timebanking.Messages.Message
import it.polito.madg34.timebanking.chat.Chat

class FirestoreRepository {
    private var fireStoreDB = FirebaseFirestore.getInstance()

    companion object {
        // Current Authenticated user
        lateinit var currentUser: FirebaseUser
    }

    /*
        Function to get the document of the current user
    */
    fun getUser(): DocumentReference {
        return fireStoreDB.collection("users").document(currentUser.email!!)
    }

    fun checkNickname(registrationNickname: String): Query {
        return fireStoreDB.collection("users").whereEqualTo("NICKNAME", registrationNickname)
    }

    /*
       Function to get the image of a users
   */
    fun getChatImage(email : String) : DocumentReference {
        return fireStoreDB.collection("users").document(email)
    }

    fun getViewUser(email: String): DocumentReference {
        return fireStoreDB.collection("users").document(email)
    }

    /*
        Function to set the document of the current user
    */
    fun setUser(value: ProfileUser): Task<Void> {
        var old_skills: MutableList<String> = mutableListOf()
        fireStoreDB.collection("users").document(currentUser.email!!).get()
            .addOnSuccessListener { it ->
                if (it != null) {
                    it.data?.forEach {
                        if (it.key == "Skills") {
                            old_skills = (it.value as Map<String, String>).keys.toMutableList()
                            old_skills.forEachIndexed { index, s ->
                                // Skill deleted
                                if (!value.skills.keys.contains(s)) {
                                    removeAdvSkillFromProfile(s)
                                } /*else if (s != value.skills.keys.elementAt(index) ){ // Skill name modified
                                    removeAdvSkillFromProfile(s)
                                }*/
                            }
                        }
                    }
                }
            }
        return fireStoreDB.collection("users").document(currentUser.email!!).set(value)
    }

    fun getUserFromEmail(email: String): DocumentReference {
        return fireStoreDB.collection("users").document(email)
    }

    fun setSkills(value: String, advKey: String) {
        // get all advs from its collection to know if there are advs (else empty list)
        var old_string = ""

        fireStoreDB.collection("skills").document(value).get().addOnCompleteListener(
            OnCompleteListener {
                if (it.isSuccessful) {
                    val skill = Skills()
                    if (it.result.getString("RELATED_ADVS") != null) {
                        old_string = it.result.getString("RELATED_ADVS")!!
                    }

                    if (old_string.isNotEmpty()) {
                        skill.relatedAdvs = "$old_string,$advKey"
                    } else {
                        skill.relatedAdvs = advKey
                    }

                    fireStoreDB.collection("skills").document(value).set(skill)

                } else {
                    Log.d("LOGGER", "No such document");
                }
            })
    }

    fun removeSkills(value: String, id: MutableList<String>) {
        fireStoreDB.collection("skills").document(value).get().addOnCompleteListener(
            OnCompleteListener {
                var old_string = ""
                if (it.isSuccessful) {
                    val skill = Skills()
                    if (it.result.getString("RELATED_ADVS") != null) {
                        old_string = it.result.getString("RELATED_ADVS")!!
                    }
                    val listAdvs = old_string.split(",") as MutableList<String>
                    Log.d("ADVS", listAdvs.toString())
                    var newString = ""
                    if (listAdvs.size > 1) {
                        id.forEach { s ->
                            if (listAdvs.contains(s)) {
                                listAdvs.removeAt(listAdvs.indexOf(s))
                            }
                        }
                        listAdvs.forEachIndexed { index, s ->
                            if (index == 0)
                                newString = s
                            else newString = "$newString,$s"
                        }
                    } else {
                        newString = ""
                    }
                    skill.relatedAdvs = newString
                    if (newString.isNotEmpty())
                        fireStoreDB.collection("skills").document(value).set(skill)
                    else
                        fireStoreDB.collection("skills").document(value).delete()
                }
            })
    }

    fun getAllSkills(): CollectionReference {

        return fireStoreDB.collection("skills")
    }

    fun getAdvs(): Query {
        return fireStoreDB.collection("advertisements")
            .whereEqualTo("PUBLISHED_BY", currentUser.email!!)
    }

    fun getAdvFromDocId(docId: String): DocumentReference? {
        if (docId.isEmpty())
            return null
        return fireStoreDB.collection("advertisements").document(docId)
    }

    fun getAllAdvs(): CollectionReference {
        return fireStoreDB.collection("advertisements")
    }

    fun saveAdvDB(value: TimeSlot): Task<Void> {
        setSkills(value.related_skill, value.id)
        return fireStoreDB.collection("advertisements").document(value.id).set(value)
    }

    fun updateAdvDB(value: TimeSlot): Task<Void> {
        return fireStoreDB.collection("advertisements").document(value.id).set(value)
    }

    /*
    * Remove adv from advertisements collection and remove RELATED_ADVS when deleted from listFragment
    * */
    fun removeAdvDB(value: TimeSlot): Task<Void> {
        // Remove the adv in the skills collection, if the string is empty delete the document
        removeSkills(value.related_skill, mutableListOf(value.id))
        Log.d("DELETE", "aaaa")
        return fireStoreDB.collection("advertisements").document(value.id).delete()
    }

    /*
    * Remove adv from advertisements collection and remove RELATED_ADVS when skill is deleted/updated from profile
    * */
    fun removeAdvSkillFromProfile(skill: String) {
        val listIds: MutableList<String> = mutableListOf()
        fireStoreDB.collection("advertisements")
            .whereEqualTo("PUBLISHED_BY", currentUser.email)
            .whereEqualTo("RELATED_SKILL", skill).get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null) {
                        task.result.documents.forEach {
                            //removeSkill(skill, it.id)
                            listIds.add(it.id)
                            fireStoreDB.collection("advertisements")
                                .document(it.id).delete()
                        }
                        removeSkills(skill, listIds)
                    }
                }
            })
    }


    fun getAllChatEmail(): DocumentReference {
        return fireStoreDB.collection("chat").document(currentUser.email!!)
    }

    fun addChat(newChat: Chat, message: Message) {
        val x = newChat.fromUser?.values
        x?.forEach {
            it.values.forEach { mex_id ->
                setMessage(mex_id, message).addOnSuccessListener {
                    fireStoreDB.collection("chat").document(currentUser.email!!).set(newChat)
                }
            }
        }
    }

    fun getChatMessages(email: String): DocumentReference {
        var chatMap = mapOf<String, String>()
        getAllChatEmail().get().addOnSuccessListener {
            if(it != null){
                chatMap = it.data?.get(email) as Map<String, String>
            }
        }

    }

    fun setMessage(id: String, message: Message): Task<Void> {
        return fireStoreDB.collection("messages").document(id).set(message)
    }


}