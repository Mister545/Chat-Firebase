package com.example.chatfirebase.ui.chats

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.RcView.ModelChat
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.ui.registration.SignInAct
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope

class ChatsViewModel : ViewModel() {

    val firebaseService = FirebaseService()
    val me: MutableLiveData<UserModel> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    var listChats: MutableLiveData<MutableList<ModelChat>> = MutableLiveData<MutableList<ModelChat>>()

    fun chatsInit() {
        getListChatsFromBd { chats ->
            chats.forEach { chat ->
                getUserModels(chat.value.participants) { arrayUserModels ->
                    val list: MutableList<ModelChat> = mutableListOf()
                    list.add(
                        ModelChat(
                            chat.key,
                            arrayUserModels,
                            chat.value.lastTime,
                            chat.value.lastMassage
                        )
                    )
                    listChats.value = list
                }
            }
        }
        getMyUserModel()
    }

    fun getMyUserModel(){
        firebaseService.getUser(uid!!){
            me.value = it
        }
    }

    private fun getUserModels(
        participants: MutableList<String>,
        callback: (ArrayList<UserModel>) -> Unit
    ) {
        val listUserModelsWithoutPhotoCheck = arrayListOf<UserModel>()

        var listUserModels = arrayListOf<UserModel>()

        participants.forEach { userId ->
            firebaseService.getUser(userId) { user ->
                listUserModelsWithoutPhotoCheck.add(user)

                if (listUserModelsWithoutPhotoCheck.size == participants.size) {
                    listUserModels = checkPhotoInProfile(listUserModelsWithoutPhotoCheck)
                    callback(listUserModels)
                }
            }
        }
    }

    private fun checkPhotoInProfile(listUserModelsWithoutPhotoCheck: ArrayList<UserModel>): ArrayList<UserModel> {

        val result = arrayListOf<UserModel>()
        listUserModelsWithoutPhotoCheck.forEach {
            if (it.image == "")
                it.image = "https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png"
            result.add(it)
        }
        return result
    }

    fun getListChatsFromBd(callback: (Map<String, ChatModel>) -> Unit)  {
        firebaseService.getUser(uid!!){ me ->
            val listChats: MutableMap<String, ChatModel> = mutableMapOf()
            if (!me.chats.isNullOrEmpty()){
                me.chats.forEach { chatCode ->
                    firebaseService.getChat(chatCode){
                        listChats[chatCode] = it
                        callback(listChats)
                    }
                }
            }
        }
    }

    fun authInit() : Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}

