package com.example.chatfirebase.ui.chat

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.TYPE_TO_DO

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
        val isToDo = intent.getStringExtra(TYPE_TO_DO)

        val fragment = ChatFragment()
        val bundle = Bundle()
        bundle.putString("clickedItem", clickedItem)
        bundle.putString("clickedUser", clickedUser)
        bundle.putString(TYPE_TO_DO, isToDo)
        Log.d("ooo", "is to do activity $isToDo")

        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
        Log.d("ooo", "intent to chat $clickedItem")


            toolbarInitForChat()
    }


    private fun toolbarInitForChat() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.p_ed_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }
    }

    private fun toolbarToDoInit() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.p_ed_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }
    }
}