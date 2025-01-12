package com.example.chatfirebase

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class FirebaseService {

    val database = FirebaseDatabase.getInstance()

    private var lastMessageId: String? = null

    fun listenerMassage(room: String, callback: (MassageModel) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val chatRef = database.getReference("chats/$room/massages")

        chatRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newMessage = snapshot.getValue(MassageModel::class.java)
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

    fun setUserState(userModel: UserModel, userUid: String){
        val databaseReference = database.getReference("users/$userUid")
        databaseReference.setValue(userModel)
    }

    fun setImage(imageModel: Map<String,String>){
        val databaseReference = database.getReference("images")
        databaseReference.setValue(imageModel)
    }

    fun setChat(chat: ChatModel, chatId: String){

        val databaseReference = database.getReference("chats/$chatId")
        databaseReference.setValue(chat)
    }

    fun getChat(chatId: String, callback: (ChatModel) -> Unit){
        val databaseReference = database.getReference("chats/$chatId")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(ChatModel::class.java) == null){
                    callback(
                        ChatModel(mutableListOf(), mutableListOf()))
                }else {
                    val chat = dataSnapshot.getValue(ChatModel::class.java)!!
                    callback(chat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }
    fun getImage(uid: String, callback: (String) -> Unit){
        val databaseReference = database.getReference("images/$uid")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, String>>() {}

                if (dataSnapshot.getValue(String::class.java) == null){
                    callback(
                        String()
                    )
                }else {
                    val image = dataSnapshot.getValue(String::class.java)
                    callback(image!!)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }

    fun getAllChats(callback: (Boolean, HashMap<String,ChatModel>) -> Unit) {
        val databaseReference = database.getReference("chats")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val chats = HashMap<String,ChatModel>()
                var isNull = true

                for (snapshot in dataSnapshot.children) {
                    val chat = snapshot.getValue(ChatModel::class.java)
                    if (chat != null) {

                        chats[snapshot.key.toString()] = chat
                        isNull = false
                    }else {
                        chats["dgdhdhtj"] = ChatModel(mutableListOf("null"))
                        isNull = true
                    }
                }
                Log.d("ooo", "getAllChats ========== $isNull, $chats")

                callback(isNull, chats)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }


    fun getUser(userUid: String, callback: (UserModel) -> Unit){
        val databaseReference = database.getReference("users/${userUid}")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.getValue(UserModel::class.java) == null){
                    println()
                }else {
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

    fun getAllUsers(callback: (HashMap<String, UserModel>) -> Unit){
        val databaseReference = database.getReference("users")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listUsers = hashMapOf<String, UserModel>()
                if (dataSnapshot.exists()) {
                    // Проходимо по всіх дочірніх елементах, якщо вони існують
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(UserModel::class.java) // Отримуємо ключ (ідентифікатор користувача)
                        user?.let {
                            listUsers[snapshot.key.toString()] = it // Додаємо ідентифікатор користувача до списку
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

    fun getEventMassages(chatId: String, callback: (MutableList<MassageModel>) -> Unit) {
        val databaseReference = database.getReference("chats/$chatId/massages")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    callback(mutableListOf(MassageModel("", "", "")))
                } else {
                    val genericTypeIndicator = object : GenericTypeIndicator<MutableList<MassageModel>>() {}
                    val chat: MutableList<MassageModel>? = dataSnapshot.getValue(genericTypeIndicator)
                    if (chat != null) {
                        callback(chat)
                    } else {
                        callback(mutableListOf(MassageModel()))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Помилка зчитування з Firebase: ${error.message}")
            }
        })
    }


}
