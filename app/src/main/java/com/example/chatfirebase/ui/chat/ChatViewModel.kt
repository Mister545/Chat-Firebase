package com.example.chatfirebase.ui.chat

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MessageModel
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.ui.ChatState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ChatViewModel(private val firebaseService: FirebaseService) : ViewModel() {
    val state = MutableLiveData<ChatState>().apply { value = ChatState() }
    private val uid = FirebaseAuth.getInstance().uid

    fun start(chatId: String?, clickedUser: String?) {
        when {
            !chatId.isNullOrEmpty() -> handleExistingChat(chatId)
            !clickedUser.isNullOrEmpty() -> handleNewChat(clickedUser)
            else -> handleToDoChat()
        }
    }

    fun sendMessage(chatId: String, messageText: String) {
        if (messageText.isBlank()) {
            state.value = state.value?.copy(error = "Повідомлення не може бути порожнім")
            return
        }

        viewModelScope.launch {
            try {
                val chatId2 = chatId.ifEmpty { getTodoChatId() }

                state.value = state.value?.copy(isLoading = true)

                // Створюємо об'єкт повідомлення
                val message = MessageModel(
                    nameMassageUid = uid!!,
                    massage = messageText,
                    time = DataTimeHelper().getIsoUtcFormat()
                )

                // Відправляємо повідомлення через FirebaseService
                firebaseService.sendMessageAsync(chatId2, message, state.value?.messages?.size ?: 0)

                // Оновлюємо список повідомлень
                val updatedMessages = firebaseService.getMessagesAsync(chatId2)
                state.value = state.value?.copy(messages = updatedMessages, isLoading = false)
            } catch (e: Exception) {
                // У разі помилки оновлюємо стан із повідомленням про помилку
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun handleExistingChat(chatId: String) {
        state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val chat = firebaseService.getChatAsync(chatId)
                val partner = getPartnerAsync(chat)
                val partners = getPartnersAsync(chat)

                state.value = state.value?.copy(
                    chatType = chat,
                    partner = partner,
                    partners = partners,
                    isLoading = false
                )

                firebaseService.getEventMassages(chatId) { messages ->
                    state.postValue(state.value?.copy(messages = messages))
                }
                Log.d("ooo", "state - ${state.value}")

            } catch (e: Exception) {
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun handleNewChat(clickedUser: String) {
        state.value = state.value?.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val partner = firebaseService.getUserAsync(clickedUser)
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
                val user = firebaseService.getUserAsync(uid!!)
                val toDoChats = user.chats?.filter { it.length == 4 } ?: emptyList()
                if (toDoChats.isNotEmpty()) {
                    val messages = firebaseService.getMessagesAsync(toDoChats[0])
                    state.value = state.value?.copy(messages = messages, isLoading = false)
                } else {
                    state.value = state.value?.copy(isLoading = false, error = "Чатів типу 'to-do' не знайдено")
                }
            } catch (e: Exception) {
                state.value = state.value?.copy(isLoading = false, error = e.message)
            }
        }
    }

    private suspend fun getPartnerAsync(chat: ChatModel): UserModel {
        val partnerId = chat.participants.first { it != uid }
        return firebaseService.getUserAsync(partnerId)
    }

    private suspend fun getPartnersAsync(chat: ChatModel): Map<String, UserModel> = coroutineScope {
        chat.participants
            .associateWith { participantId ->
                async { firebaseService.getUserAsync(participantId) } // Запускаємо асинхронний запит
            }
            .mapValues { it.value.await() } // Очікуємо результати всіх запитів
    }


    private suspend fun getTodoChatId(): String = coroutineScope {
        async {
            firebaseService.getUserAsync(uid!!).chats?.firstOrNull { code ->
                firebaseService.getChatAsync(code).typeOfChat == TYPE_TO_DO
            } ?: ""
        }.await()
    }

    fun deleteUserFromGrope(clickedItem: Item, items: ArrayList<Item>, chatId: String){
        viewModelScope.launch{
            val deletedUser = firebaseService.getUserAsync(clickedItem.userUid)
            deletedUser.chats!!.remove(chatId)
            firebaseService.setUserState(deletedUser, clickedItem.userUid)

            val chat = firebaseService.getChatAsync(chatId)
            chat.participants.remove(clickedItem.userUid)
            firebaseService.setChat(chat, chatId)

            start(chatId, "")
        }
    }

    fun changeNameOfGrope(chatId: String, nameOfGrope: String) {
        viewModelScope.launch {
            val chat = firebaseService.getChatAsync(chatId)
            chat.nameOfGroup = nameOfGrope
            firebaseService.setChat(chat, chatId)
            start(chatId, "")
        }
    }

    fun changeImageOfGrope(chatId: String, uri: Uri) {
        viewModelScope.launch {
            val chat = firebaseService.getChatAsync(chatId)
            chat.imageOfGroup = uri.toString()
            firebaseService.setChat(chat, chatId)
            start(chatId, "")
        }
    }

    fun editMessage(chatId: String, messageId: String, newText: String) {
        firebaseService.updateMessageText(chatId, messageId, newText)
    }

    fun deleteMessage(chatId: String, messageId: String) {
        firebaseService.deleteMessageFromChat(chatId, messageId)
    }


}
