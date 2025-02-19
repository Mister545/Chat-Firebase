package com.example.chatfirebase.model.repository

import android.net.Uri
import com.example.chatfirebase.data.Chat
import com.example.chatfirebase.data.Message
import com.example.chatfirebase.data.User
import com.google.firebase.storage.UploadTask

interface FirebaseRepository {

    fun getUser(userUid: String, callback: (User) -> Unit)

    suspend fun getUserAsync(userId: String): User

    fun setUserState(user: User, userUid: String): Boolean

    fun setUserState(user: User, userUid: String, callback: (Boolean) -> Unit)

    fun updateMessageText(chatId: String, messageId: String, newText: String)

    fun deleteMessageFromChat(chatId: String, messageId: String)

    suspend fun sendMessageListAsync(chatId: String, messageList: ArrayList<Pair<String, Message>>)

    suspend fun createNewConversation(
        chatId: String,
        newUserId: String,
        uid: String,
        message: Message
    )

    fun setChat(chat: Chat, chatId: String)

    fun uploadChatImage(imageUri: Uri, callback: (UploadTask) -> Unit)

    suspend fun getMessagesAsync(chatId: String): ArrayList<Pair<String, Message>>

    suspend fun getChatAsync(chatId: String): Chat

    fun getAllChats(callback: (Boolean, HashMap<String, Chat>) -> Unit)

    fun getUserSingleTime(userUid: String, callback: (User) -> Unit)

    fun getEventMassages(chatId: String, callback: (ArrayList<Pair<String, Message>>) -> Unit)
}