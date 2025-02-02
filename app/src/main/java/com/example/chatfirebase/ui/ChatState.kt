package com.example.chatfirebase.ui

import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.MessageModel
import com.example.chatfirebase.UserModel

data class ChatState(
    val chatType: ChatModel? = null,
    val partner: UserModel? = null,
    var messages: ArrayList<Pair<String, MessageModel>> = arrayListOf(),
    val partners: Map<String, UserModel> = mapOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)
