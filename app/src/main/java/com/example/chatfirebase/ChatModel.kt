package com.example.chatfirebase

data class ChatModel (
    val participants: MutableList<String> = mutableListOf(),
    val messages: ArrayList<MessageModel> = arrayListOf(),
    val lastMassage: String = "",
    val lastTime: String = "",
    val typeOfChat: String = "",
    var imageOfGroup: String? = "",
    var nameOfGroup: String? = "",
)