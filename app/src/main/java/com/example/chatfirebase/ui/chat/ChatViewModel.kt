package com.example.chatfirebase.ui.chat

import android.annotation.SuppressLint
import android.health.connect.datatypes.units.Mass
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import java.time.Instant
import java.time.ZoneOffset
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MassageModel
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatViewModel : ViewModel() {

    private val firebaseService = FirebaseService()
    var partner: MutableLiveData<UserModel> = MutableLiveData()
    var listChat: MutableLiveData<
            MutableList<MassageModel>> = MutableLiveData<MutableList<MassageModel>>()
    private val uid = FirebaseAuth.getInstance().uid


    fun getMassages(chatId: String){

        Log.d("ooo", "uid $uid")
            firebaseService.getEventMassages(chatId) { massages ->
                listChat.value = massages
        }
    }

    fun sentMassage(massage: String, chatId: String){
        val currentTime = DataTimeHelper().getIsoUtcFormat()

        firebaseService.getChat(chatId){ chat ->
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



    fun getPartner(chatId: String){
        firebaseService.getChat(chatId){
            val partnerUId = it.participants.filter { it != uid }
            firebaseService.getUser(partnerUId[0]){
                partner.value = it
            }
        }
    }
    fun start(chatId: String) {
        getPartner(chatId)
    }
}