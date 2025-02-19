package com.example.chatfirebase.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatfirebase.data.Chat
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.data.Message
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.data.User
import com.example.chatfirebase.data.ChatState
import com.example.chatfirebase.view.adapters.Item
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ChatViewModel(private val firebaseRepositoryImpl: FirebaseRepositoryImpl) : ViewModel() {
    val state = MutableLiveData<ChatState>().apply { value = ChatState() }
    val chatId2: MutableLiveData<String> = MutableLiveData("")
    private val uid = FirebaseAuth.getInstance().uid

    fun start(chatId: String?, clickedUser: String?) {
        when {
            !chatId.isNullOrEmpty() -> {
                handleExistingChat(chatId)
                chatId2.value = chatId!!
            }
            !clickedUser.isNullOrEmpty() -> handleNewChat(clickedUser)
            else -> handleToDoChat()
        }
//        messagesListener()
    }

    fun sendMessage(newUserId: String?, chatId: String?, messageText: String) {
        if (messageText.isBlank()) {
            state.value = state.value?.copy(error = "Повідомлення не може бути порожнім")
            return
        }

        viewModelScope.launch {
            try {
                chatId2.value = if (chatId.isNullOrEmpty()) chatId2.value else chatId
                if (!newUserId.isNullOrEmpty() && chatId2.value.isNullOrEmpty()) {
                    chatId2.value = sendMessageToNewUser(newUserId, messageText)
                } else if (!chatId2.value.isNullOrEmpty()) {
                    sendMessageToUser(chatId2.value!!, messageText)
                }
            } catch (e: Exception) {
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun handleExistingChat(chatId: String) {
        state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val chat = firebaseRepositoryImpl.getChatAsync(chatId)
                val partner = getPartnerAsync(chat)
                val partners = getPartnersAsync(chat)

                state.value = state.value?.copy(
                    chatType = chat,
                    partner = partner,
                    partners = partners,
                    isLoading = false
                )

                firebaseRepositoryImpl.getEventMassages(chatId) { messages ->
                    state.postValue(state.value?.copy(messages = messages))
                }
                Log.d("ooo", "state - ${state.value}")

            } catch (e: Exception) {
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

//    private fun messagesListener(){
//        if (!chatId2.value.isNullOrEmpty()) {
//                firebaseService.getEventMassages(chatId2.value) {
//                    state.value?.messages = it
//            }
//        }
//    }

    private fun handleNewChat(clickedUser: String) {
        state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val partner = firebaseRepositoryImpl.getUserAsync(clickedUser)
                state.value = state.value?.copy(partner = partner, isLoading = false)
            } catch (e: Exception) {
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun handleToDoChat() {
        state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val user = firebaseRepositoryImpl.getUserAsync(uid!!)
                val toDoChats = user.chats?.filter { it.length == 4 } ?: emptyList()
                if (toDoChats.isNotEmpty()) {
                    val messages = firebaseRepositoryImpl.getMessagesAsync(toDoChats[0])
                    state.value = state.value?.copy(messages = messages, isLoading = false)
                } else {
                    state.value = state.value?.copy(
                        isLoading = false,
                        error = "Чатів типу 'to-do' не знайдено"
                    )
                }
            } catch (e: Exception) {
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun getPartnerAsync(chat: Chat): User {
        val partnerId = chat.participants.first { it != uid }
        return firebaseRepositoryImpl.getUserAsync(partnerId)
    }

    private suspend fun getPartnersAsync(chat: Chat): Map<String, User> = coroutineScope {
        chat.participants
            .associateWith { participantId ->
                async { firebaseRepositoryImpl.getUserAsync(participantId) } // Запускаємо асинхронний запит
            }
            .mapValues { it.value.await() } // Очікуємо результати всіх запитів
    }


    private suspend fun getTodoChatId(): String = coroutineScope {
        async {
            firebaseRepositoryImpl.getUserAsync(uid!!).chats?.firstOrNull { code ->
                firebaseRepositoryImpl.getChatAsync(code).typeOfChat == TYPE_TO_DO
            } ?: ""
        }.await()
    }

    fun deleteUserFromGrope(clickedItem: Item, items: ArrayList<Item>, chatId: String) {
        viewModelScope.launch {
            val deletedUser = firebaseRepositoryImpl.getUserAsync(clickedItem.userUid)
            deletedUser.chats!!.remove(chatId)
            firebaseRepositoryImpl.setUserState(deletedUser, clickedItem.userUid)

            val chat = firebaseRepositoryImpl.getChatAsync(chatId)
            chat.participants.remove(clickedItem.userUid)
            firebaseRepositoryImpl.setChat(chat, chatId)

            start(chatId, "")
        }
    }

    fun changeNameOfGrope(chatId: String, nameOfGrope: String) {
        viewModelScope.launch {
            val chat = firebaseRepositoryImpl.getChatAsync(chatId)
            chat.nameOfGroup = nameOfGrope
            firebaseRepositoryImpl.setChat(chat, chatId)
            start(chatId, "")
        }
    }

    fun changeImageOfGrope(chatId: String, uri: Uri) {
        viewModelScope.launch {
            val chat = firebaseRepositoryImpl.getChatAsync(chatId)
            chat.imageOfGroup = uri.toString()
            firebaseRepositoryImpl.setChat(chat, chatId)
            start(chatId, "")
        }
    }

    fun editMessage(chatId: String, newText: String, messageId: String) {
        firebaseRepositoryImpl.updateMessageText(chatId, messageId, newText)
    }

    fun deleteMessage(chatId: String, messageId: String) {
        firebaseRepositoryImpl.deleteMessageFromChat(chatId, messageId)
    }

    private suspend fun sendMessageToUser(chatId: String, messageText: String) {
        val chatId2 = chatId.ifEmpty { getTodoChatId() }
        Log.d("ooo", "chat id2 in sendMessageToUser- $chatId2")

        state.value = state.value?.copy(isLoading = true)

        val message = Message(
            nameMassageUid = uid!!,
            message = messageText,
            time = DataTimeHelper().getIsoUtcFormat()
        )

        val listMassages = firebaseRepositoryImpl.getMessagesAsync(chatId2)
        listMassages.add(Pair(listMassages.size.toString(), message))
        firebaseRepositoryImpl.sendMessageListAsync(chatId2, listMassages)

        val chatState = firebaseRepositoryImpl.getChatAsync(chatId2)
        firebaseRepositoryImpl.setChat(chatState.copy(lastTime = DataTimeHelper().getIsoUtcFormat(),
            lastMassage = messageText), chatId2)


        val updatedMessages = firebaseRepositoryImpl.getMessagesAsync(chatId2)
        state.value = state.value?.copy(messages = updatedMessages, isLoading = false)
    }

    private suspend fun sendMessageToNewUser(newUserId: String, messageText: String): String {
        val message = Message(
            nameMassageUid = uid!!,
            message = messageText,
            time = DataTimeHelper().getIsoUtcFormat()
        )

        val chatId = (10000..999999).random().toString()
        firebaseRepositoryImpl.createNewConversation(
            chatId = chatId,
            newUserId = newUserId,
            uid = uid,
            message
        )
        state.value = state.value?.copy(isLoading = true)

        // Відправляємо повідомлення через FirebaseService
//        firebaseService.sendMessageAsync(chatId, message, state.value?.messages?.size ?: 0)

        // Оновлюємо список повідомлень
        val updatedMessages = firebaseRepositoryImpl.getMessagesAsync(chatId)
        state.value = state.value?.copy(messages = updatedMessages, isLoading = false)
        return chatId
    }
}
