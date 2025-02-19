package com.example.chatfirebase.model.repository

import android.net.Uri
import android.util.Log
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.data.Chat
import com.example.chatfirebase.data.Message
import com.example.chatfirebase.data.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseRepositoryImpl : FirebaseRepository {

    private val database = FirebaseDatabase.getInstance()
    private var valueEventListener: ValueEventListener? = null

    fun removeChatsListener() {
        val databaseReference = database.getReference("chats")
        valueEventListener?.let {
            databaseReference.removeEventListener(it)
            valueEventListener = null
        }
    }

    override fun updateMessageText(chatId: String, messageId: String, newText: String) {
        val messageRef =
            database.getReference("chats").child(chatId).child("messages").child(messageId)
        messageRef.child("message").setValue(newText)
        messageRef.child("lastMassage").setValue("$newText was edited")
    }

    override fun deleteMessageFromChat(chatId: String, messageId: String) {
        val messageRef =
            database.getReference("chats").child(chatId).child("messages").child(messageId)
        messageRef.removeValue()
    }

    override suspend fun createNewConversation(
        chatId: String,
        newUserId: String,
        uid: String,
        message: Message
    ) {
        val chat = Chat(
            participants = mutableListOf(uid, newUserId),
            messages = arrayListOf(message),
            lastMassage = message.message!!, lastTime = DataTimeHelper().getIsoUtcFormat(),
            typeOfChat = TYPE_CHAT, imageOfGroup = "", nameOfGroup = ""
        )
        setChat(chatId = chatId, chat = chat)
        val me = getUserAsync(uid)
        me.chats!!.add(chatId)
        setUserState(userUid = uid, user = me)
        val otherUser = getUserAsync(newUserId)
        otherUser.chats!!.add(chatId)
        setUserState(userUid = newUserId, user = otherUser)
    }

    fun removeUsersListener() {
        val databaseReference = database.getReference("users")
        valueEventListener?.let {
            databaseReference.removeEventListener(it)
            valueEventListener = null
        }
    }

    override fun setUserState(user: User, userUid: String): Boolean {
        val databaseReference = database.getReference("users/$userUid")
        return databaseReference.setValue(user).isSuccessful
    }

    override fun setUserState(user: User, userUid: String, callback: (Boolean) -> Unit) {
        val databaseReference = database.getReference("users/$userUid")
        databaseReference.setValue(user).addOnCompleteListener {
            callback(it.isSuccessful)
        }
    }

    override fun setChat(chat: Chat, chatId: String) {
        val databaseReference = database.getReference("chats/$chatId")
        databaseReference.setValue(chat)
    }

    override fun uploadChatImage(imageUri: Uri, callback: (UploadTask) -> Unit) {
        Log.d("ooo", "image URI : ${imageUri.lastPathSegment}")
        val storageRef =
            FirebaseStorage.getInstance().reference.child("imagesChat/${imageUri.lastPathSegment}")
        val uploadTask = storageRef.putFile(imageUri)

        callback(uploadTask)
    }

    override suspend fun getMessagesAsync(chatId: String): ArrayList<Pair<String, Message>> {
        return suspendCancellableCoroutine { continuation ->
            getEventMassages(chatId) { messages ->
                if (continuation.isActive) {
                    continuation.resume(messages)
                }
            }
        }
    }

    override suspend fun getChatAsync(chatId: String): Chat =
        suspendCancellableCoroutine { continuation ->
            val databaseReference = database.getReference("chats/$chatId")
            databaseReference
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        continuation.resume(chat)
                    } else {
                        continuation.resumeWithException(Exception("Chat not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    fun getChatEvent(chatId: String, callback: (Chat) -> Unit) {
        val databaseReference = database.getReference("chats/$chatId")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chat = dataSnapshot.getValue(Chat::class.java)
                if (chat != null) {
                    callback(chat)
                } else {
                    Log.e("Firebase", "Chat not found")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error: ${databaseError.message}")
            }
        })
    }


    override suspend fun getUserAsync(userId: String): User =
        suspendCancellableCoroutine { continuation ->
            val databaseReference = database.getReference("users/$userId")

            databaseReference.get()
                .addOnSuccessListener { dataSnapshot ->
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        continuation.resume(user)
                    } else {
                        continuation.resumeWithException(Exception("User not found"))
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    override suspend fun sendMessageListAsync(chatId: String, messageList: ArrayList<Pair<String, Message>>) {
        val chatRef = FirebaseDatabase.getInstance()
            .getReference("chats").child(chatId)
            .child("messages")

        // Перетворюємо список Pair у Map
        val messageMap = messageList.associate { it.first to it.second }

        try {
            chatRef.setValue(messageMap).await()
            Log.d("ooo", "Messages sent successfully")
        } catch (e: Exception) {
            Log.e("ooo", "Failed to send messages", e)
        }
    }

    override fun getAllChats(callback: (Boolean, HashMap<String, Chat>) -> Unit) {
        val databaseReference = database.getReference("chats")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chats = HashMap<String, Chat>()
                var isNull = true

                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat != null) {
                        chats[snapshot.key.toString()] = chat
                        isNull = false
                    } else {
                        chats["dgdhdhtj"] = Chat(mutableListOf("null"))
                        isNull = true
                    }
                }
                Log.d("ooo", "getAllChats ========== $isNull, $chats")

                callback(isNull, chats)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Помилка зчитування з Firebase: ${error.message}")
            }
        }

        // Додаємо слухача
        databaseReference.addValueEventListener(valueEventListener!!)
    }

    override fun getUser(userUid: String, callback: (User) -> Unit) {
        val databaseReference = database.getReference("users/${userUid}")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(User::class.java) == null) {
                    println()
                } else {
                    val userSnap = dataSnapshot.getValue(User::class.java)!!
                    callback(userSnap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)

    }

    override fun getUserSingleTime(userUid: String, callback: (User) -> Unit) {
        val databaseReference = database.getReference("users/${userUid}")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(User::class.java) == null) {
                    println()
                } else {
                    val userSnap = dataSnapshot.getValue(User::class.java)!!
                    callback(userSnap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }

    fun getAllUsers(callback: (HashMap<String, User>) -> Unit) {
        val databaseReference = database.getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listUsers = hashMapOf<String, User>()
                if (dataSnapshot.exists()) {
                    // Проходимо по всіх дочірніх елементах, якщо вони існують
                    for (snapshot in dataSnapshot.children) {
                        val user =
                            snapshot.getValue(User::class.java) // Отримуємо ключ (ідентифікатор користувача)
                        user?.let {
                            listUsers[snapshot.key.toString()] =
                                it // Додаємо ідентифікатор користувача до списку
                        }
                    }
                } else {
                    Log.d("Firebase", "users null")
                }
                callback(listUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }

    override fun getEventMassages(chatId: String, callback: (ArrayList<Pair<String, Message>>) -> Unit) {
        val databaseReference = database.getReference("chats/$chatId/messages")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messagesList = arrayListOf<Pair<String, Message>>()

                for (messageSnapshot in dataSnapshot.children) {
                    val key = messageSnapshot.key // Отримання ключа
                    val message = messageSnapshot.getValue(Message::class.java) // Отримання об'єкта

                    if (key != null && message != null) {
                        messagesList.add(Pair(key, message))
                    }
                }
                if (messagesList.isEmpty()) {
                    messagesList.add(Pair("", Message("", "", "")))
                }

                callback(messagesList)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)
    }
}
