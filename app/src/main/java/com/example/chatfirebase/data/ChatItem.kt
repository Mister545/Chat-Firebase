package com.example.chatfirebase.data

data class ChatItem (
    val codeChat: String,
    val names: ArrayList<User>,
    val lastTime: String,
    val lastMassage: String,
    val imageOfGroup: String? = "",
    val nameOfGroup: String? = "",
    val typeOfChat: String
)
