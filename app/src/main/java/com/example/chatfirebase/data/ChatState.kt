package com.example.chatfirebase.data

data class ChatState(
    val chatType: Chat? = null,
    val partner: User? = null,
    var messages: ArrayList<Pair<String, Message>> = arrayListOf(),
    val partners: Map<String, User> = mapOf(),
    val isLoading: Boolean = false,
    val error: String? = null
)
