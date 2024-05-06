package es.ulpgc.pigs.fitquest.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class FirebaseUserRepository : UserRepository {
    private val database = Firebase.firestore
    override fun createUser(user: User, callback: (Result) -> Unit) {
        findUserByUsername(user.getName()){ existingUser ->
            if(existingUser != null){
                callback(Result.Error(Exception("Username ${user.getName()} is already in use.")))
                return@findUserByUsername
            }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener{
                if(it.isSuccessful){
                    val firebaseUser = it.result?.user
                    val userMap = hashMapOf(
                        "username" to user.getName(),
                        "email" to user.getEmail(),
                        "isDoctor" to user.isDoctor(),
                        "pictureURL" to null,
                        "level" to 1,
                        "xp" to 0,
                        "points" to 0,
                        "achievements" to listOf<String>()
                    )
                    if (firebaseUser != null) {
                        try {
                            database.collection("users").document(firebaseUser.uid).set(userMap)
                            callback(Result.GeneralSuccess(true))
                        } catch (e: Exception) {
                            callback(Result.Error(e))
                        }
                    } else{
                        callback(Result.Error(Exception("Unknown error")))
                    }
                } else{
                    it.exception?.let{exception ->
                        callback(Result.Error(exception))
                    }
                }
            }
        }
    }

    override fun findUserByUsername(username: String, callback: (User?) -> Unit) {
        database.collection("users").whereEqualTo("username", username).get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                callback(null)
            } else {
                callback(documentToUser(documents.documents[0]))
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }

    override fun findUserByEmail(email: String, callback: (User?) -> Unit) {
        database.collection("users").whereEqualTo("email", email).get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                callback(null)
            } else {
                callback(documentToUser(documents.documents[0]))
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }

    override fun updateUser(user: User, callback: (Result) -> Unit) {
        val userMap = userToMap(user)
        database.collection("users").document(user.getId()).update(userMap as Map<String, Any>).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(Result.GeneralSuccess(true))
            } else {
                it.exception?.let { exception ->
                    callback(Result.Error(exception))
                }
            }
        }
    }

    override fun searchUsers(query: String, callback: (SearchResult) -> Unit) {
        database.collection("users").whereGreaterThanOrEqualTo("username", query).whereLessThanOrEqualTo("username", query + "\uf8ff").get().addOnSuccessListener { documents ->
            val users = documents.map { document ->
                documentToUser(document)
            }
            callback(SearchResult.Results(users))
        }.addOnFailureListener {
            callback(SearchResult.ShowError(it))
        }
    }

    override fun getAllDoctors(callback: (List<User>) -> Unit) {
        database.collection("users").whereEqualTo("isDoctor", true).get().addOnSuccessListener { documents ->
            val doctors = documents.map { document ->
                documentToUser(document)
            }
            callback(doctors)
        }
    }

    private fun documentToUser(document: DocumentSnapshot): User {
        return User(
            id = document.id,
            name = document.getString("username") ?: "",
            password = "", // we don't store passwords in firestore
            email = document.getString("email") ?: "",
            isDoctor = document.getBoolean("isDoctor") ?: false,
            pictureURL = document.getString("pictureURL"),
            level = document.getLong("level")?.toInt() ?: 1,
            xp = document.getLong("xp")?.toInt() ?: 0,
            points = document.getLong("points")?.toInt() ?: 0,
            achievements = document.get("achievements") as? List<String> ?: listOf(),
            steps = document.getLong("steps")?.toInt() ?: 0,
            stepGoal = document.getLong("stepGoal")?.toInt() ?: 0
        )
    }

    private fun userToMap(user: User): Map<String, Any?> {
        return mapOf(
            "username" to user.getName(),
            "email" to user.getEmail(),
            "isDoctor" to user.isDoctor(),
            "pictureURL" to user.getPictureURL(),
            "level" to user.getLevel(),
            "xp" to user.getXp(),
            "points" to user.getPoints(),
            "achievements" to user.getAchievements(),
            "steps" to user.getSteps(),
            "stepGoal" to user.getStepGoal()
        )
    }

}