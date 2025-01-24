package com.example.chatfirebase

data class ChatModel (
    val participants: MutableList<String> = mutableListOf(),
    val massages: MutableList<MassageModel> = mutableListOf(),
    val lastMassage: String = "",
    val lastTime: String = "",
    val typeOfChat: String = "",
    val imageOfGroup: String? = "",
    val nameOfGroup: String? = "",
)