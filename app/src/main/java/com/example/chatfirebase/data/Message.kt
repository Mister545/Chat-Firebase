package com.example.chatfirebase.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val nameMassageUid: String? = "",
    val message: String? = "",
    val image: String? = "",
    val time: String? = ""
): Parcelable
