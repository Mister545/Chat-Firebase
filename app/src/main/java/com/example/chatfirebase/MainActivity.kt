package com.example.chatfirebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatfirebase.RcView.ModelChat
import com.example.chatfirebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    val firebaseService = FirebaseService()
    val auth = FirebaseAuth.getInstance()
    lateinit var binding: ActivityMainBinding

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        val codeChat: ModelChat = intent.getParcelableExtra("clickedItem")!!


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                    1001
//                )
//            }
//        }
//
//        val user = codeChat.names.filter { it.name != auth.currentUser!!.uid }
//        binding.textView2.text = "${user[0].name} |"
//
//        firebaseService.getEventMassages(codeChat.codeChat) {
//            adapter.setList(it)
//            binding.rcView.layoutManager = LinearLayoutManager(this)
//            binding.rcView.adapter = adapter
//
//            val isAtBottom = !binding.rcView.canScrollVertically(1)
//
//            if (isAtBottom) {
//                binding.rcView.scrollToPosition(adapter.itemCount - 1)
//            }
//
//            firebaseService.getEventMassages(codeChat.codeChat) {
//                adapter.setList(it)
//
//                adapter.notifyDataSetChanged()
//
//                val isAtBottom = !binding.rcView.canScrollVertically(1)
//
//                if (isAtBottom) {
//                    binding.rcView.scrollToPosition(adapter.itemCount - 1)
//                }
//            }
//        }
//
//        binding.bSend.setOnClickListener {
//            val text = binding.editTextText.text.toString().trim()
//            if (text == "")
//                return@setOnClickListener
//            else {
//                binding.editTextText.text.clear()
//                firebaseService.getAllUsers { users ->
//                    users.forEach {
//                        if (it.value.name == user[0].name) {
//                            val calendar = Calendar.getInstance()
//                            val dateFormat = SimpleDateFormat("HH:mm") // формат для годин і хвилин
//                            val time = dateFormat.format(calendar.time)
//                            firebaseService.getChat(codeChat.codeChat) { chat ->
//
//                                val massages = chat.massages
//                                massages.add(
//                                    MessageModel(
//                                        auth.currentUser!!.uid,
//                                        text,
//                                        users[auth.currentUser!!.uid]!!.image,
//                                        time
//                                    )
//                                )
//
//                                firebaseService.setChat(
//                                    ChatModel(
//                                        chat.participants,
//                                        massages,
//                                        text,
//                                        time
//                                    ),
//                                    codeChat.codeChat
//                                )
//                            }
//
//                        }
//                    }
//                }
//            }
//        }

//        val mRef = databse.getReference("chats/${codeChat.codeChat}")
        Log.d("ooo", "codeChat ========== chats/$codeChat")

    }
}