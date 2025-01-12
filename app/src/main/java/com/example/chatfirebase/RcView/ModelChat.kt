package com.example.chatfirebase.RcView

import android.os.Parcelable
import com.example.chatfirebase.UserModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelChat (
    val codeChat: String,
    val names: ArrayList<UserModel>,
    val lastTime: String,
    val lastMassage: String
): Parcelable
