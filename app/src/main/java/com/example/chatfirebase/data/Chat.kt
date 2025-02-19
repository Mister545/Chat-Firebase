package com.example.chatfirebase.data

data class Chat (
    val participants: MutableList<String> = mutableListOf(),
    val messages: ArrayList<Message> = arrayListOf(),
    val lastMassage: String = "",
    val lastTime: String = "",
    val typeOfChat: String = "",
    var imageOfGroup: String? = "",
    var nameOfGroup: String? = "",
)