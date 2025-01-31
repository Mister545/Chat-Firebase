package com.example.chatfirebase.ui.settings

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val user: MutableLiveData<UserModel> = MutableLiveData()
    val imageUri: MutableLiveData<Uri> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    val firebaseService = FirebaseService()

    init {
        firebaseService.getUser(uid!!) {
            user.value = it
        }
    }

    fun uploadImageUri(uri: Uri) {
        imageUri.value = uri
        val u = user.value
        u!!.image = if (imageUri.isInitialized) imageUri.value.toString() else ""
        firebaseService.setUserState(
            userModel = u,
            userUid = uid!!
        )
    }

    fun exitAccount() {
        firebaseAuth.signOut()
    }
}