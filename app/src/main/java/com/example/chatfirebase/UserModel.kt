package com.example.chatfirebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var chats: MutableList<String>? = mutableListOf(),
    val email: String = "",
    var name: String = "",
    var image: String = "",
    var introduceYourSelf: String? = "",
    var dateOfBerth: String? = "0.0.0000"
    ): Parcelable
