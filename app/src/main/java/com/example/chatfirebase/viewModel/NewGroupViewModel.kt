package com.example.chatfirebase.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.data.Chat
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.data.User
import com.google.firebase.auth.FirebaseAuth
import java.util.Timer
import kotlin.concurrent.schedule

class NewGroupViewModel : ViewModel() {
    val firebaseRepositoryImpl = FirebaseRepositoryImpl()
    val uid = FirebaseAuth.getInstance().uid
    val users: MutableLiveData<HashMap<String, User>> = MutableLiveData()
    val imageUri: MutableLiveData<Uri> = MutableLiveData()

    init {
        start()
    }

    private fun start(){
        firebaseRepositoryImpl.getAllUsers {
            users.value = it.filter { it.key != uid } as HashMap
        }
    }

    fun makeGroup(nameGroup: String, selectedUsers: HashMap<String, User> ){
        val randomCodeChat = (10000..99999).random()
        val participants: MutableList<String> = selectedUsers.keys.toMutableList()
        participants.add(uid!!)
        firebaseRepositoryImpl.setChat(
            Chat(
            participants = participants, lastTime = DataTimeHelper().getIsoUtcFormat(), typeOfChat = TYPE_GROUP, imageOfGroup = imageUri.value.toString(), nameOfGroup = nameGroup
        ),randomCodeChat.toString())

        setCodesChatsToUser(participants, randomCodeChat.toString())
    }

    private fun setCodesChatsToUser(users: MutableList<String>, randomCodeChat: String){
        Log.d("ooo", "users size ${users.size}")
        Log.d("ooo", "users ${users}")
        users.forEach{
            firebaseRepositoryImpl.getUserSingleTime(it){ userModel ->
                Log.d("ooo", "userModel got ${userModel}")
                val timer = Timer()
                timer.schedule(0, 100) {}
                Thread.sleep(600)
                timer.cancel()
                    val userChats = userModel.chats
                    userChats!!.add(randomCodeChat)
                    userModel.chats = userChats
                val s = firebaseRepositoryImpl.setUserState(
                        userModel,
                        it
                    )
                    Log.d("ooo", "is Successful ${s}")
            }
        }
    }

    fun uploadImageUri(uri: Uri){
        firebaseRepositoryImpl.uploadChatImage(uri){
            it.addOnSuccessListener {
                imageUri.value = uri
                Log.d("ooo", "Image Successful ${it}")
            }
            it.addOnFailureListener {
                Log.d("ooo", "Image Failure ${it}")
            }
        }
    }

    fun destroy(){
        firebaseRepositoryImpl.removeChatsListener()
    }
}