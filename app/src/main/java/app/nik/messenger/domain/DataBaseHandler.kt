package app.nik.messenger.domain

import android.text.TextUtils
import android.util.Log
import app.nik.messenger.data.Message
import app.nik.messenger.data.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import java.time.Instant
class DataBaseHandler {
    private val mDb = Firebase.firestore
    private val mUsersCollection = mDb.collection("users")
    private val mMessagesCollection = mDb.collection("messages")

    suspend fun findUserIdByName(name: String): String? {
        var userId: String? = null
        try {
            val querySnapshot: QuerySnapshot = mUsersCollection
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                userId = documentSnapshot.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return userId
    }
    suspend fun setUserName(user: User): Boolean {
        val isUnique = isUserNameUnique(user.name)
        if (!isUnique || user.id == null) {
            // Если имя не уникально, возвращаем false
            return false
        }

        var status = true
        val userMap = mapOf(
            "name" to user.name
        )

        status = try {
            mUsersCollection.document(user.id)
                .set(userMap)
                .await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
        return status
    }


    suspend fun isUserNameUnique(name: String): Boolean {
        return try {
            val querySnapshot: QuerySnapshot = mUsersCollection
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .await()

            // Если запрос вернул пустой результат, значит имя уникально
            querySnapshot.isEmpty
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun sendMessage(msg: Message): Boolean {
        return try {
            if (TextUtils.equals(msg.senderId, msg.receiverId)) {
                return false
            }

            val messageMap = mapOf(
                "senderId" to msg.senderId,
                "receiverId" to msg.receiverId,
                "content" to msg.content,
                "timestamp" to msg.timestamp
            )

            val (senderId, receiverId) = if (msg.senderId.compareTo(msg.receiverId) < 0) {
                Pair(msg.senderId, msg.receiverId)
            } else {
                Pair(msg.receiverId, msg.senderId)
            }

            val docRef = mMessagesCollection.document("${senderId}_${receiverId}")
            val docSnapshot = docRef.get().await()

            if (docSnapshot.exists()) {
                // If document exists, update it
                docRef.update("messages", FieldValue.arrayUnion(messageMap)).await()
            } else {
                // If document does not exist, create it with initial message
                docRef.set(mapOf("messages" to listOf(messageMap))).await()
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun getMessages(senderId: String, receiverId: String): List<Message> {
        val (sender, receiver) = if (senderId < receiverId) {
            Pair(senderId, receiverId)
        } else {
            Pair(receiverId, senderId)
        }

        val documentId = "${sender}_${receiver}"
        val documentSnapshot = mMessagesCollection.document(documentId).get().await()
        val messagesData = documentSnapshot.get("messages") as? List<Map<String, Any>>

        return messagesData?.map { messageData ->
            Message(
                senderId = messageData["senderId"] as String,
                receiverId = messageData["receiverId"] as String,
                content = messageData["content"] as String,
                timestamp = messageData["timestamp"] as Long
            )
        } ?: emptyList()
    }

    suspend fun userNameExistForId(userId: String): Boolean {
        return try {
            val documentSnapshot = mUsersCollection.document(userId).get().await()
            documentSnapshot.exists() && documentSnapshot.getString("name") != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    companion object{
        const val TAG = "DataBaseHandler"
    }
}