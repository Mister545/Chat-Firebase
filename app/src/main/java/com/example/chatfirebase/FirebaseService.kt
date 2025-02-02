package com.example.chatfirebase

import android.net.Uri
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseService {

    val database = FirebaseDatabase.getInstance()

    private var valueEventListener: ValueEventListener? = null

    private var lastMessageId: String? = null

    fun removeChatsListener() {
        val databaseReference = database.getReference("chats")
        valueEventListener?.let {
            databaseReference.removeEventListener(it)
            valueEventListener = null
        }
    }

    fun updateMessageText(chatId: String, messageId: String, newText: String) {
        val messageRef =
            database.getReference("chats").child(chatId).child("messages").child(messageId)
        messageRef.child("message").setValue(newText)
        messageRef.child("lastMassage").setValue("$newText was edited")
    }

    fun deleteMessageFromChat(chatId: String, messageId: String) {
        val messageRef =
            database.getReference("chats").child(chatId).child("messages").child(messageId)
        messageRef.removeValue()
    }

    suspend fun createNewConversation(
        chatId: String,
        newUserId: String,
        uid: String,
        message: MessageModel
    ) {
        val chatModel = ChatModel(
            participants = mutableListOf(uid, newUserId),
            messages = arrayListOf(message),
            lastMassage = message.message!!, lastTime = DataTimeHelper().getIsoUtcFormat(),
            typeOfChat = TYPE_CHAT, imageOfGroup = "", nameOfGroup = ""
        )
        setChat(chatId = chatId, chat = chatModel)
        val me = getUserAsync(uid)
        me.chats!!.add(chatId)
        setUserState(userUid = uid, userModel = me)
        val otherUser = getUserAsync(newUserId)
        otherUser.chats!!.add(chatId)
        setUserState(userUid = newUserId, userModel = otherUser)
    }

    fun removeUsersListener() {
        val databaseReference = database.getReference("users")
        valueEventListener?.let {
            databaseReference.removeEventListener(it)
            valueEventListener = null
        }
    }


    fun listenerMassage(room: String, callback: (MessageModel) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("chats/$room/messages")

        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newMessage = snapshot.getValue(MessageModel::class.java)
                Log.d("ooo", "newMassage ========== $newMessage")

                if (newMessage != null) {
                    callback(newMessage)

                    lastMessageId = snapshot.key
                    Log.d("ooo", "newMassage ========== $newMessage")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    fun setUserState(userModel: UserModel, userUid: String): Boolean {
        val databaseReference = database.getReference("users/$userUid")
        return databaseReference.setValue(userModel).isSuccessful
    }

    fun setUserState(userModel: UserModel, userUid: String, callback: (Boolean) -> Unit) {
        val databaseReference = database.getReference("users/$userUid")
        databaseReference.setValue(userModel).addOnCompleteListener {
            callback(it.isSuccessful)
        }
    }

    fun delBase() {
        val databaseReference = database.getReference()
        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ooo", "Дані успішно видалені!")
            } else {
                Log.e("ooo", "Помилка видалення даних: ${task.exception?.message}")
            }
        }
    }

    fun setImage(imageModel: Map<String, String>) {
        val databaseReference = database.getReference("images")
        databaseReference.setValue(imageModel)
    }

    fun setChat(chat: ChatModel, chatId: String) {
        val databaseReference = database.getReference("chats/$chatId")
        databaseReference.setValue(chat)
    }

    fun getChat(chatId: String, callback: (ChatModel) -> Unit) {
        val databaseReference = database.getReference("chats/$chatId")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(ChatModel::class.java) == null) {
                    callback(
                        ChatModel(mutableListOf(), arrayListOf())
                    )
                } else {
                    val chat = dataSnapshot.getValue(ChatModel::class.java)!!
                    callback(chat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }

    fun uploadChatImage(imageUri: Uri, callback: (UploadTask) -> Unit) {
        Log.d("ooo", "image URI : ${imageUri.lastPathSegment}")
        val storageRef =
            FirebaseStorage.getInstance().reference.child("imagesChat/${imageUri.lastPathSegment}")
        val uploadTask = storageRef.putFile(imageUri)

        callback(uploadTask)
    }

    fun uploadProfileImage(imageUri: Uri, callback: (UploadTask) -> Unit) {
        Log.d("ooo", "image URI : ${imageUri.lastPathSegment}")
        val storageRef =
            FirebaseStorage.getInstance().reference.child("imagesProfile/${imageUri.lastPathSegment}")
        val uploadTask = storageRef.putFile(imageUri)

        callback(uploadTask)
    }


    suspend fun getMessagesAsync(chatId: String): ArrayList<Pair<String, MessageModel>> {
        return suspendCancellableCoroutine { continuation ->
            getEventMassages(chatId) { messages ->
                if (continuation.isActive) {
                    continuation.resume(messages)
                }
            }
        }
    }


    suspend fun getChatAsync(chatId: String): ChatModel =
        suspendCancellableCoroutine { continuation ->
            val databaseReference = database.getReference("chats/$chatId")
            databaseReference
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    val chat = dataSnapshot.getValue(ChatModel::class.java)
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


    suspend fun getUserAsync(userId: String): UserModel =
        suspendCancellableCoroutine { continuation ->
            val databaseReference = database.getReference("users/$userId")

            databaseReference.get()
                .addOnSuccessListener { dataSnapshot ->
                    val user = dataSnapshot.getValue(UserModel::class.java)
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


    fun getImage(uid: String, callback: (String) -> Unit) {
        val databaseReference = database.getReference("images/$uid")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, String>>() {}

                if (dataSnapshot.getValue(String::class.java) == null) {
                    callback(
                        String()
                    )
                } else {
                    val image = dataSnapshot.getValue(String::class.java)
                    callback(image!!)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }

    suspend fun sendMessageListAsync(chatId: String, messageList: ArrayList<Pair<String, MessageModel>>) {
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



    fun getAllChats(callback: (Boolean, HashMap<String, ChatModel>) -> Unit) {
        val databaseReference = database.getReference("chats")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chats = HashMap<String, ChatModel>()
                var isNull = true

                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(ChatModel::class.java)
                    if (chat != null) {
                        chats[snapshot.key.toString()] = chat
                        isNull = false
                    } else {
                        chats["dgdhdhtj"] = ChatModel(mutableListOf("null"))
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


    fun getUser(userUid: String, callback: (UserModel) -> Unit) {
        val databaseReference = database.getReference("users/${userUid}")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(UserModel::class.java) == null) {
                    println()
                } else {
                    val userSnap = dataSnapshot.getValue(UserModel::class.java)!!
                    callback(userSnap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        }
        databaseReference.addValueEventListener(valueEventListener!!)

    }

    fun getUserSingleTime(userUid: String, callback: (UserModel) -> Unit) {
        val databaseReference = database.getReference("users/${userUid}")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(UserModel::class.java) == null) {
                    println()
                } else {
                    val userSnap = dataSnapshot.getValue(UserModel::class.java)!!
                    callback(userSnap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }

//    fun getAllUsers(callback: (MutableList<UserModel>) -> Unit){
//            val databaseReference = database.getReference("users")
//            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    val users = mutableListOf<UserModel>()
//                    if (dataSnapshot.getValue(UserModel::class.java) == null){
//                        println()
//                    }else {
//                        val userSnap = dataSnapshot.getValue(UserModel::class.java)!!
//                        users.add(userSnap)
//                    }
//                    callback(users)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    println("Помилка зчитування з Firebase: ${error.message}")
//                }
//            })
//    }

    fun getAllUsers(callback: (HashMap<String, UserModel>) -> Unit) {
        val databaseReference = database.getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listUsers = hashMapOf<String, UserModel>()
                if (dataSnapshot.exists()) {
                    // Проходимо по всіх дочірніх елементах, якщо вони існують
                    for (snapshot in dataSnapshot.children) {
                        val user =
                            snapshot.getValue(UserModel::class.java) // Отримуємо ключ (ідентифікатор користувача)
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

    fun getEventMassages(chatId: String, callback: (ArrayList<Pair<String, MessageModel>>) -> Unit) {
        val databaseReference = database.getReference("chats/$chatId/messages")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val messagesList = arrayListOf<Pair<String, MessageModel>>()

                for (messageSnapshot in dataSnapshot.children) {
                    val key = messageSnapshot.key // Отримання ключа
                    val message = messageSnapshot.getValue(MessageModel::class.java) // Отримання об'єкта

                    if (key != null && message != null) {
                        messagesList.add(Pair(key, message))
                    }
                }

                // Якщо повідомлення немає, передаємо список з пустим елементом
                if (messagesList.isEmpty()) {
                    messagesList.add(Pair("", MessageModel("", "", "")))
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
