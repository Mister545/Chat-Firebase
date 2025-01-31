package com.example.chatfirebase.ui.chats

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.RcView.ModelChat
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {

    private val firebaseService = FirebaseService()
    val me: MutableLiveData<UserModel> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    val listChats: MutableLiveData<List<ModelChat>?> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()

    fun chatsInit() {
        if (uid != null) {
            getMyListChatsFromBd()
            getMyUserModel()
        } else {
            Log.e("ChatsViewModel", "User ID is null")
        }
    }

    private fun getMyUserModel() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val user = firebaseService.getUserAsync(uid!!)
                me.value = user
            } catch (e: Exception) {
                Log.e("ChatsViewModel", "Error fetching user model: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun getMyListChatsFromBd() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val user = firebaseService.getUserAsync(uid!!)
                val listChatsC = mutableListOf<ModelChat>()

                user.chats?.forEach { chatCode ->
                    val chat = firebaseService.getChatAsync(chatCode)
                    if (chat.typeOfChat != TYPE_TO_DO) {
                        val userModels = getUserModels(chat.participants)
                        listChatsC.add(
                            ModelChat(
                                codeChat = chatCode,
                                nameOfGroup = chat.nameOfGroup,
                                imageOfGroup = chat.imageOfGroup,
                                names = userModels as ArrayList,
                                lastTime = chat.lastTime,
                                lastMassage = chat.lastMassage,
                                typeOfChat = chat.typeOfChat
                            )
                        )
                    }
                }

                listChats.value = listChatsC
            } catch (e: Exception) {
                Log.e("ChatsViewModel", "Error fetching chats: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun getUserModels(participants: List<String>): List<UserModel> {
        val userModels = participants.map { userId ->
            firebaseService.getUserAsync(userId)
        }

        return checkPhotoInProfile(userModels)
    }

    private fun checkPhotoInProfile(users: List<UserModel>): List<UserModel> {
        return users.map {
            if (it.image.isBlank()) {
                it.image = "https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png"
            }
            it
        }
    }

    fun authInit(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}
