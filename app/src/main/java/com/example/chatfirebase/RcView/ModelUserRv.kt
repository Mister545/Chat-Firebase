package com.example.chatfirebase.RcView

import android.media.Image
import android.net.Uri
import android.os.Parcelable
import com.example.chatfirebase.UserModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelUserRv (
    val codeChat: String,
    val names: ArrayList<UserModel>,
    val lastTime: String,
    val lastMassage: String
): Parcelable
