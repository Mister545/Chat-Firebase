package com.example.chatfirebase.ui.chat

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MassageModel
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class ChatViewModel : ViewModel() {

    private val firebaseService = FirebaseService()
    val group: MutableLiveData<ChatModel> = MutableLiveData()
    var partner: MutableLiveData<UserModel> = MutableLiveData()
    var listChat: MutableLiveData<
            MutableList<MassageModel>> = MutableLiveData<MutableList<MassageModel>>()
    private val uid = FirebaseAuth.getInstance().uid

    private fun getMassagesWithChatCode(chatId: String) {

        Log.d("ooo", "uid $uid")
        firebaseService.getEventMassages(chatId) { massages ->
            if (massages.size == 1 && massages[0].time.isNullOrEmpty())
                Log.d("ooo", "massages is empty")
            else
                listChat.value = massages

            Log.d("ooo", "massages is $massages")

        }
    }

    private fun getMassagesWithUserCode() {
        listChat.value = mutableListOf()
    }

    fun sentMassage(massage: String, typeOfChat: String?, chatId: String?, userCode: String?) {
        if (!chatId.isNullOrEmpty()) {
            sentMassageWithChatCode(massage, chatId)
        } else if (!userCode.isNullOrEmpty()) {
            sentMassageWithUserCode(massage, userCode)
        } else if (typeOfChat == TYPE_TO_DO) {
            Log.d("ooo", "chat cod ${chatId}")
            ///toDo chat
            FirebaseService().getUser(FirebaseAuth.getInstance().uid!!) {
                val codeSaved = it.chats!!.filter { it.length == 4 }
                Log.d("ooo", "chat cod ${codeSaved[0]}")

                sentMassageWithChatCode(massage, codeSaved[0])
            }
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
                    chat.participants,
                    chat.massages,
                    massage,
                    currentTime,
                    chat.typeOfChat,
                    chat.imageOfGroup,
                    chat.nameOfGroup
                ), chatId
            )
        }
    }

    private fun sentMassageWithUserCode(massage: String, userCode: String) {
        val currentTime = DataTimeHelper().getIsoUtcFormat()

        val chatId = addNewChat(massage = massage, user = userCode, currentTime = currentTime)
        getMassagesWithChatCode(chatId)
    }


    private fun getPartner(chatModel: ChatModel) {
        val partnerUId = chatModel.participants.filter { it != uid }
        firebaseService.getUser(partnerUId[0]) {
            partner.value = it
        }
    }

    private fun getGroup(chatModel: ChatModel) {
        group.value = chatModel
    }

    private fun getChatData(chatId: String, type: String) {
        firebaseService.getChat(chatId) {
            Log.d("ooo", "typeeeeeeeeeeeeeeeeeeee $type")

            if (type == TYPE_GROUP) {
                getGroup(it)
            } else {
                getPartner(it)
            }
        }
    }

    private fun getPartnerWithUserCode(userCode: String) {
        firebaseService.getUser(userCode) {
            partner.value = it
        }
    }

    fun start(chatId: String?, clickedUser: String?) {
        Log.d("ooo", "chatId $chatId click User $clickedUser")
        if (!chatId.isNullOrEmpty()) {
            firebaseService.getChat(chatId) {
                    getChatData(chatId, it.typeOfChat)
                    getMassagesWithChatCode(chatId)
            }
        } else if (!clickedUser.isNullOrEmpty()) {
            getPartnerWithUserCode(clickedUser)             //// add new chat
            getMassagesWithUserCode()
        } else {
            FirebaseService().getUser(FirebaseAuth.getInstance().uid!!) { it ->
                val codeSaved = it.chats!!.filter { it.length == 4 }
                Log.d("ooo", "chat cod ${codeSaved[0]}")      ///toDo chat
                getMassagesWithChatCode(codeSaved[0])
            }
        }
    }

    private fun addNewChat(user: String, massage: String, currentTime: String): String {
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
            typeOfChat = TYPE_CHAT,
            lastMassage = massage,
            lastTime = currentTime
        )
        firebaseService.setChat(chatModel, randomCode)

        firebaseService.getUserSingleTime(uid) {
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
        firebaseService.getUserSingleTime(user) {
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

    fun destroy(){
        firebaseService.removeChatsListener()
    }
}