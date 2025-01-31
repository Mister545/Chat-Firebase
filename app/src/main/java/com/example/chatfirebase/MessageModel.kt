package com.example.chatfirebase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageModel(
    val nameMassageUid: String? = "",
    val massage: String? = "",
    val image: String? = "",
    val time: String? = ""
): Parcelable
