package com.example.chatfirebase.ui.saved

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MassageModel
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class SavedViewModel : ViewModel() {

    private val firebaseService = FirebaseService()
    var partner: MutableLiveData<UserModel> = MutableLiveData()
    var listChat: MutableLiveData<
            MutableList<MassageModel>> = MutableLiveData<MutableList<MassageModel>>()
    private val uid = FirebaseAuth.getInstance().uid

    private fun getMassagesWithChatCode(chatId: String) {

        Log.d("ooo", "uid $uid")
        firebaseService.getEventMassages(chatId) { massages ->
            listChat.value = massages
        }
    }

    private fun getMassagesWithUserCode() {
        listChat.value = mutableListOf()
    }

    fun sentMassage(massage: String, chatId: String?, userCode: String?) {
        if (!chatId.isNullOrEmpty()) {

            sentMassageWithChatCode(massage, chatId)
        } else if (!userCode.isNullOrEmpty()) {
            sentMassageWithUserCode(massage, userCode)
        }
    }

    private fun sentMassageWithChatCode(massage: String, chatId: String) {
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
                    participants = chat.participants,
                    massages = chat.massages,
                    lastMassage = massage,
                    lastTime = currentTime,
                    typeOfChat = chat.typeOfChat
                ), chatId
            )
        }
    }

    private fun sentMassageWithUserCode(massage: String, userCode: String) {
        val currentTime = DataTimeHelper().getIsoUtcFormat()

        val chatId = addNewChat(massage = massage, user = userCode, currentTime = currentTime)
        getMassagesWithChatCode(chatId)
    }


    private fun getPartnerWithChatCode(chatId: String) {
        firebaseService.getChat(chatId) {
            val partnerUId = it.participants.filter { it != uid }
            firebaseService.getUser(partnerUId[0]) {
                partner.value = it
            }
        }
    }

    private fun getPartnerWithUserCode(userCode: String) {
        firebaseService.getUser(userCode) {
            partner.value = it
        }
    }

    fun start(chatId: String?, clickedUser: String?) {
        if (!chatId.isNullOrEmpty() && chatId.length > 5) {
            getPartnerWithChatCode(chatId)
            getMassagesWithChatCode(chatId)
        } else if (!clickedUser.isNullOrEmpty()) {
            getPartnerWithUserCode(clickedUser)
            getMassagesWithUserCode()
        } else {
            getMassagesWithChatCode(chatId!!)
        }
    }

    private fun addNewChat(user: String, massage: String, currentTime: String): String {
        val randomCode = (100000..999999).random().toString()
        val chatModel = ChatModel(
            participants = mutableListOf(uid!!, user),
            massages = mutableListOf(
                MassageModel(
                    nameMassageUid = uid,
                    massage = massage,
                    image = "",
                    time = currentTime
                )
            ),
            typeOfChat = TYPE_CHAT,
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

    fun destroy() {
        firebaseService.removeChatsListener()
    }
}