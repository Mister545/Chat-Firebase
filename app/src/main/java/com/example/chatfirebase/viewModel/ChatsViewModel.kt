package com.example.chatfirebase.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.data.ChatItem
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.data.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {

    private val firebaseRepositoryImpl = FirebaseRepositoryImpl()
    val me: MutableLiveData<User> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    var listChats: MutableLiveData<List<ChatItem>?> = MutableLiveData()
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
                val user = firebaseRepositoryImpl.getUserAsync(uid!!)
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
                val user = firebaseRepositoryImpl.getUserAsync(uid!!)
                val listChatsC = mutableListOf<ChatItem>()
                user.chats?.forEach { chatCode ->
                    firebaseRepositoryImpl.getChatEvent(chatCode) { chat ->
                        viewModelScope.launch {
                            val userModels = getUserModels(chat.participants)
                            if (chat.typeOfChat != TYPE_TO_DO) {
                                listChatsC.add(
                                    ChatItem(
                                        codeChat = chatCode,
                                        nameOfGroup = chat.nameOfGroup,
                                        imageOfGroup = chat.imageOfGroup,
                                        names = userModels as ArrayList,
                                        lastTime = chat.lastTime,
                                        lastMassage = chat.lastMassage,
                                        typeOfChat = chat.typeOfChat
                                    )
                                )
                                listChats.postValue(listChatsC)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("ChatsViewModel", "Error fetching chats: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }


    private suspend fun getUserModels(participants: List<String>): List<User> {
        val userModels = participants.map { userId ->
            firebaseRepositoryImpl.getUserAsync(userId)
        }

        return checkPhotoInProfile(userModels)
    }

    private fun checkPhotoInProfile(users: List<User>): List<User> {
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
