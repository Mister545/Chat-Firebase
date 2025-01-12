package com.example.chatfirebase.ui.chats

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.RcView.ModelChat
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.ui.registration.SignInAct
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.coroutineScope

class ChatsViewModel : ViewModel() {

    val firebaseService = FirebaseService()
    val uid = FirebaseAuth.getInstance().uid
    var listChats: MutableLiveData<MutableList<ModelChat>> = MutableLiveData<MutableList<ModelChat>>()

    fun chatsInit() {
        getListChatsFromBd { chats ->
            chats.forEach{ chat ->
                getUserModels(chat.value.participants) { arrayUserModels ->
                    val list: MutableList<ModelChat> = mutableListOf()
                    list.add(
                        ModelChat(
                            chat.key,
                            arrayUserModels,
                            chat.value.lastTime,
                            chat.value.lastMassage
                        )
                    )
                    listChats.value = list
                }
            }











        }
//        listChats.value =
//            mutableListOf(
//                ModelChat(
//                    "kdjg",
//                    arrayListOf(
//                        UserModel(
//                            mutableListOf(),
//                            "",
//                            "Denys",
//                            "https://www.pluggedin.com/wp-content/uploads/2020/01/the-simpsons-review-image-1024x587.jpg"
//                        ),
//                        UserModel(
//                            mutableListOf(),
//                            "",
//                            "Vlad",
//                            "https://play.google.com/store/tv/show/The_Simpsons?id=snmuy8AqAyY&hl=uk"
//                        )
//                    ),
//                    "18:30",
//                    "hi Denys"
//                )
//            )
    }

    private fun getUserModels(
        participants: MutableList<String>,
        callback: (ArrayList<UserModel>) -> Unit
    ) {
        val listUserModelsWithoutPhotoCheck = arrayListOf<UserModel>()

        var listUserModels = arrayListOf<UserModel>()

        participants.forEach { userId ->
            firebaseService.getUser(userId) { user ->
                listUserModelsWithoutPhotoCheck.add(user)

                if (listUserModelsWithoutPhotoCheck.size == participants.size) {
                    listUserModels = checkPhotoInProfile(listUserModelsWithoutPhotoCheck)
                    callback(listUserModels)
                }
            }
        }
    }

    private fun checkPhotoInProfile(listUserModelsWithoutPhotoCheck: ArrayList<UserModel>): ArrayList<UserModel> {

        val result = arrayListOf<UserModel>()
        listUserModelsWithoutPhotoCheck.forEach {
            if (it.image == "")
                it.image = "https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png"
            result.add(it)
        }
        return result
    }


    fun getListChatsFromBd(callback: (Map<String, ChatModel>) -> Unit)  {
        firebaseService.getAllChats(){ isNull, chats ->
            if (isNull){
                Log.d("ooo","null Chats list")
            }else{
                val filteredChats = chats.filter { (_, chatModel) ->
                    chatModel.participants.contains(uid)
                }
                Log.d("ooo","filtered chats $filteredChats")
                Log.d("ooo","my uid $uid")
                callback(filteredChats)
            }
        }
    }

    fun authInit() : Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}

