package it.polito.madg34.timebanking

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import java.util.*

class FirestoreRepository {
    private var fireStoreDB = FirebaseFirestore.getInstance()

    companion object{
        // Current Authenticated user
        lateinit var currentUser: FirebaseUser
    }

    /*
        Function to get the document of the current user
    */
    fun getUser(): DocumentReference {
        return fireStoreDB.collection("users").document(currentUser.email!!)
    }

    /*
       Function to get the documents of all users
   */
    fun getOthersUser(): CollectionReference {
        return fireStoreDB.collection("users")
    }

    /*
        Function to set the document of the current user
    */
    fun setUser(value : ProfileUser): Task<Void> {
        val old_user = getUser()
        old_user.get().addOnCompleteListener(OnCompleteListener {
            var old_skills: Set<String>? = null
            if(it.isSuccessful){
                old_skills = it.result.get("Skills") as Set<String>?
                old_skills?.forEachIndexed { index, s ->
                    if(s != value.skills.keys.elementAt(index) ){
                        removeAdvSkillFromProfile(s)
                    }
                }
            }
        })
        return fireStoreDB.collection("users").document(currentUser.email!!).set(value)
    }

    fun setSkills(value : String, advKey : String) {
        // get all advs from its collection to know if there are advs (else empty list)
        var old_string = ""

         fireStoreDB.collection("skills").document(value).get().addOnCompleteListener(
            OnCompleteListener{
            if(it.isSuccessful) {
                val skill = Skills()
                if(it.result.getString("RELATED_ADVS") != null){
                    old_string = it.result.getString("RELATED_ADVS")!!
                }

                if(old_string.isNotEmpty()){
                    skill.relatedAdvs = "$old_string,$advKey"
                }else{
                    skill.relatedAdvs = advKey
                }

                 fireStoreDB.collection("skills").document(value).set(skill)

            }else {
                Log.d("LOGGER", "No such document");
            }
        })
    }

    fun removeSkill(value : String, id : String){
        fireStoreDB.collection("skills").document(value).get().addOnCompleteListener(
            OnCompleteListener{
                var old_string = ""
                if(it.isSuccessful) {
                    val skill = Skills()
                    if(it.result.getString("RELATED_ADVS") != null){
                        old_string = it.result.getString("RELATED_ADVS")!!
                    }
                    val listAdvs = old_string.split(",").filter {  it != id }
                    var newString = ""
                    listAdvs.forEachIndexed { index, s ->
                        if(index == 0){
                            newString = s
                        }else{
                            newString = "$newString,$s"
                        }
                    }
                    skill.relatedAdvs = newString
                    fireStoreDB.collection("skills").document(value).set(skill)
                }
            })
    }

    fun getAdvs(): Query{
        return fireStoreDB.collection("advertisements").whereEqualTo("PUBLISHED_BY", currentUser.email!!)
    }

    fun getAllAdvs(): CollectionReference{
        return fireStoreDB.collection("advertisements")
    }

    fun saveAdvDB(value : TimeSlot): Task<Void> {
        setSkills(value.related_skill, value.id)
        return fireStoreDB.collection("advertisements").document(value.id).set(value)
    }

    fun updateAdvDB(value : TimeSlot) : Task<Void>{
        return fireStoreDB.collection("advertisements").document(value.id).set(value)
    }

    /*
    * Remove adv from advertisements collection and remove RELATED_ADVS when deleted from listFragment
    * */
    fun removeAdvDB(value : TimeSlot) : Task<Void> {
        removeSkill(value.related_skill, value.id)
        return fireStoreDB.collection("advertisements").document(value.id).delete()
    }

    /*
    * Remove adv from advertisements collection and remove RELATED_ADVS when skill is deleted/updated from profile
    * */
    fun removeAdvSkillFromProfile(skill : String){
        fireStoreDB.collection("advertisements")
            .whereEqualTo("PUBLISHED_BY", currentUser.email)
            .whereEqualTo("RELATED_SKILL",skill).get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if(task.isSuccessful){
                    if(task.result != null){
                        task.result.documents.forEach{
                            removeSkill(skill, it.id)
                            fireStoreDB.collection("advertisements")
                                .document(it.id).delete()
                        }
                    }
                }
            })
    }
}