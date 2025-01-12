package com.example.chatfirebase.ui.chat

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.ScrollActivity2
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.ui.profile.ProfileFragment

class ChatActivity : AppCompatActivity() {

    val firebaseService = FirebaseService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val clickedItem = intent.getStringExtra("clickedItem")
        val clickedUser = intent.getStringExtra("clickedUser")

        val fragment = Chat()
        val bundle = Bundle()
        bundle.putString("clickedItem", clickedItem)
        bundle.putString("clickedUser", clickedUser)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
        Log.d("ooo", "intent to chat $clickedItem")

        toolbarInit2()

    }

    private fun toolbarInit() {
        val toolbar: Toolbar = findViewById(R.id.custom_toolbar)

        toolbar.title = "Chat"
        toolbar.setLogo(R.drawable.user_default)
//        toolbar.setNavigationIcon()
        setSupportActionBar(toolbar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.p_ed_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun toolbarInit2() {
//        val toolbar: Toolbar = findViewById(R.id.custom_toolbar)
        window.statusBarColor = ContextCompat.getColor(this, R.color.p_ed_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }

    }
}