package com.example.chatfirebase

data class ChatModel (
    val participants: MutableList<String> = mutableListOf(),
    val massages: MutableList<MessageModel> = mutableListOf(),
    val lastMassage: String = "",
    val lastTime: String = "",
    val typeOfChat: String = "",
    var imageOfGroup: String? = "",
    var nameOfGroup: String? = "",
)