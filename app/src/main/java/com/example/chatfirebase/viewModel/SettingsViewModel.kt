package com.example.chatfirebase.viewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.data.User
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel : ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val user: MutableLiveData<User> = MutableLiveData()
    val imageUri: MutableLiveData<Uri> = MutableLiveData()
    val uid = FirebaseAuth.getInstance().uid
    val firebaseRepositoryImpl = FirebaseRepositoryImpl()

    init {
        firebaseRepositoryImpl.getUser(uid!!) {
            user.value = it
        }
    }

    fun uploadImageUri(uri: Uri) {
        imageUri.value = uri
        val u = user.value
        u!!.image = if (imageUri.isInitialized) imageUri.value.toString() else ""
        firebaseRepositoryImpl.setUserState(
            user = u,
            userUid = uid!!
        )
    }

    fun exitAccount() {
        firebaseAuth.signOut()
    }
}