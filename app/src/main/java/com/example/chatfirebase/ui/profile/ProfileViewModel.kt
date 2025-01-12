package com.example.chatfirebase.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    val user: MutableLiveData<UserModel> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    val firebaseService = FirebaseService()

    fun init(){
        firebaseService.getUser(uid!!){
            user.value = it
        }
    }
}