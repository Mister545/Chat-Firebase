package com.example.chatfirebase

data class ChatModel (
    val participants: MutableList<String> = mutableListOf(),
    var massages: MutableList<MassageModel> = mutableListOf(),
    val lastMassage: String = "",
    val lastTime: String = ""
)