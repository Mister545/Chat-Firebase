package com.example.chatfirebase.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MassageModel
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class ChatViewModel : ViewModel() {

    private val firebaseService = FirebaseService()
    var partner: MutableLiveData<UserModel> = MutableLiveData()
    var listChat: MutableLiveData<
            MutableList<MassageModel>> = MutableLiveData<MutableList<MassageModel>>()
    private val uid = FirebaseAuth.getInstance().uid

    fun getMassagesWithChatCode(chatId: String) {

        Log.d("ooo", "uid $uid")
        firebaseService.getEventMassages(chatId) { massages ->
            listChat.value = massages
        }
    }

    fun getMassagesWithUserCode() {
        listChat.value = mutableListOf()
    }

    fun sentMassage(massage: String, chatId: String?, userCode: String?) {
        if (!chatId.isNullOrEmpty()) {
            sentMassageWithChatCode(massage, chatId)
        } else if (!userCode.isNullOrEmpty()) {
            sentMassageWithUserCode(massage, userCode)
        }
    }

    fun sentMassageWithChatCode(massage: String, chatId: String) {
        val currentTime = DataTimeHelper().getIsoUtcFormat()

        firebaseService.getChat(chatId) { chat ->
            chat.massages.add(
                MassageModel(
                    uid,
                    massage,
                    image = "",
                    currentTime
                )
            )

            firebaseService.setChat(
                ChatModel(
                    chat.participants,
                    chat.massages,
                    massage,
                    currentTime
                ), chatId
            )
        }
    }

    fun sentMassageWithUserCode(massage: String, userCode: String) {
        val currentTime = DataTimeHelper().getIsoUtcFormat()

        val chatId = addNewChat(massage = massage, user = userCode, currentTime = currentTime)
        getMassagesWithChatCode(chatId)
    }


    fun getPartnerWithChatCode(chatId: String) {
        firebaseService.getChat(chatId) {
            val partnerUId = it.participants.filter { it != uid }
            firebaseService.getUser(partnerUId[0]) {
                partner.value = it
            }
        }
    }

    fun getPartnerWithUserCode(userCode: String) {
        firebaseService.getUser(userCode) {
            partner.value = it
        }
    }

    fun start(chatId: String?, clickedUser: String?) {
        if (!chatId.isNullOrEmpty()) {
            getPartnerWithChatCode(chatId)
            getMassagesWithChatCode(chatId)
        } else if (!clickedUser.isNullOrEmpty()) {
            getPartnerWithUserCode(clickedUser)
            getMassagesWithUserCode()
        }
    }

    private fun addNewChat(user: String, massage: String, currentTime: String) : String{
        val randomCode = (100000..999999).random().toString()
        val chatModel = ChatModel(
            participants = mutableListOf(uid!!, user),
            massages = mutableListOf(
                MassageModel(
                    uid,
                    massage,
                    "",
                    currentTime
                )
            ),
            lastMassage = massage,
            lastTime = currentTime
        )
        firebaseService.setChat(chatModel, randomCode)

        firebaseService.getUser(uid) {
            val arr = mutableListOf<String>()  // Список
            val chatsToAdd: MutableList<String> = it.chats ?: mutableListOf()
            arr.addAll(chatsToAdd)
            arr.add(randomCode)

            firebaseService.setUserState(
                userModel =
                UserModel(
                    arr,
                    name = it.name,
                    email = it.email,
                    image = it.image
                ), uid
            )
        }
        firebaseService.getUser(user) {
            val arr = mutableListOf<String>()
            val chatsToAdd: MutableList<String> = it.chats ?: mutableListOf()
            arr.addAll(chatsToAdd)
            arr.add(randomCode)

            firebaseService.setUserState(
                userModel =
                UserModel(
                    arr,
                    name = it.name,
                    email = it.email,
                    image = it.image
                ), user
            )
        }
        return randomCode
    }
}