package com.example.chatfirebase.model.repository

import android.net.Uri

interface SharedPrefsRepository {

    fun saveImage(uri: Uri)
}