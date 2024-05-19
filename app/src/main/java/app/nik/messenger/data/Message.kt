package app.nik.messenger.data

import java.io.Serializable
import java.time.Instant

data class Message(
    val content : String,
    val senderId : String,
    val receiverId : String,
    val timestamp : Long
) : Serializable