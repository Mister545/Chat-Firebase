package com.example.chatfirebase.ui.newGroup

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

class NewGroupViewModel : ViewModel() {
    val firebaseService = FirebaseService()
    val uid = FirebaseAuth.getInstance().uid
    val users: MutableLiveData<HashMap<String, UserModel>> = MutableLiveData()
    val imageUri: MutableLiveData<Uri> = MutableLiveData()

    init {
        start()
    }

    private fun start(){
        firebaseService.getAllUsers {
            users.value = it.filter { it.key != uid } as HashMap
        }
    }

    fun makeGroup(nameGroup: String, selectedUsers: HashMap<String, UserModel> ){
        val randomCodeChat = (10000..99999).random()
        val participants: MutableList<String> = selectedUsers.keys.toMutableList()
        participants.add(uid!!)
        firebaseService.setChat(ChatModel(
            participants = participants, lastTime = DataTimeHelper().getIsoUtcFormat(), typeOfChat = TYPE_GROUP, imageOfGroup = imageUri.value.toString(), nameOfGroup = nameGroup
        ),randomCodeChat.toString())

        setCodesChatsToUser(participants, randomCodeChat.toString())
    }

    private fun setCodesChatsToUser(users: MutableList<String>, randomCodeChat: String){
        Log.d("ooo", "users size ${users.size}")
        Log.d("ooo", "users ${users}")
        users.forEach{
            firebaseService.getUserSingleTime(it){ userModel ->
                Log.d("ooo", "userModel got ${userModel}")
                val timer = Timer()
                timer.schedule(0, 100) {}
                Thread.sleep(600)
                timer.cancel()
                    val userChats = userModel.chats
                    userChats!!.add(randomCodeChat)
                    userModel.chats = userChats
                val s = firebaseService.setUserState(
                        userModel,
                        it
                    )
                    Log.d("ooo", "is Successful ${s}")
            }
        }
    }

    fun uploadImageUri(uri: Uri){
        firebaseService.uploadChatImage(uri){
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
        firebaseService.removeChatsListener()
    }
}