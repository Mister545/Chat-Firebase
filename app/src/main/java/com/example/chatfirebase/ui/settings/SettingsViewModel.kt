package com.example.chatfirebase.ui.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthSettings

class SettingsViewModel : ViewModel() {
    val firebaseAuth = FirebaseAuth.getInstance()

    fun exitAccount() {
        firebaseAuth.signOut()
    }
    // TODO: Implement the ViewModel
}