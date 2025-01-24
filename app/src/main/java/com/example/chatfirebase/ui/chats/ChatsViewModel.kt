package com.example.chatfirebase.ui.chats

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.RcView.ModelChat
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class ChatsViewModel : ViewModel() {

    val firebaseService = FirebaseService()
    val me: MutableLiveData<UserModel> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    var listChats: MutableLiveData<MutableList<ModelChat>> =
        MutableLiveData<MutableList<ModelChat>>()

    fun chatsInit() {
        getMyListChatsFromBd { chats ->
            chats.forEach { chat ->
                getUserModels(chat.value.participants) { arrayUserModels ->
                    val list: MutableList<ModelChat> = mutableListOf()
                    list.add(
                        ModelChat(
                            codeChat = chat.key,
                            nameOfGroup = chat.value.nameOfGroup,
                            imageOfGroup = chat.value.imageOfGroup,
                            names = arrayUserModels,
                            lastTime = chat.value.lastTime,
                            lastMassage = chat.value.lastMassage,
                            typeOfChat = chat.value.typeOfChat
                        )
                    )
                    Log.d("ooo", "listCHATS from bd ${chats.size}")
                    Log.d("ooo", "user models array ${arrayUserModels}")
                    Log.d("ooo", "listCHATS ${list.size}")
                    Log.d("ooo", "listCHATS ${list.size}")
                    listChats.value = list
                }
            }
        }
        getMyUserModel()
    }

    fun destroy(){
        firebaseService.removeChatsListener()
    }

    private fun getMyUserModel() {
        firebaseService.getUser(uid!!) {
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

    private fun getMyListChatsFromBd(callback: (Map<String, ChatModel>) -> Unit) {
        firebaseService.getUser(uid!!) { me ->
            val listChats: MutableMap<String, ChatModel> = mutableMapOf()
            if (!me.chats.isNullOrEmpty()) {
                me.chats!!.forEach { chatCode ->
                    firebaseService.getChat(chatCode) {
                        if (it.typeOfChat != TYPE_TO_DO) {
                            listChats[chatCode] = it
                            callback(listChats)
                        }
                    }
                }
            }
        }
    }

    fun authInit(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}

