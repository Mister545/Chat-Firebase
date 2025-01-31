package com.example.chatfirebase.ui.registration

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MessageModel
import com.example.chatfirebase.R
import com.example.chatfirebase.ScrollActivity
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInAct : AppCompatActivity() {

    lateinit var binding: ActivitySignInBinding
    lateinit var auth: FirebaseAuth
    var reg = true
    val firebase = FirebaseService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        auth = FirebaseAuth.getInstance()

        binding.tvAuth.setOnClickListener {
            updateUI()
            reg = !reg
        }


        binding.btnRegister.setOnClickListener {

            registerUserWithData()
        }
    }

    private fun delBase() {
        firebase.delBase()

    }

    private fun register(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    setRegisterUserStateInBD(auth.currentUser!!, name.trimEnd(), (1000..9999).random().toString())
                    val intent = Intent(this, ScrollActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "add $name", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    fun auth(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")


                    val intent = Intent(this, ScrollActivity::class.java)
                    startActivity(intent)
                } else {
                    binding.progressBar.visibility = View.GONE
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun updateUI() {
        if (reg) {
            binding.apply {
                tvTitle.text = "Autentification"
                tvAuth.text = "Regestration"
                btnRegister.text = "Autentification"
            }
        }
        if (!reg) {
            binding.apply {
                tvTitle.text = "Regestration"
                tvAuth.text = "Autentification"
                btnRegister.text = "Regestration"
            }
        }
    }

    private fun setRegisterUserStateInBD(user: FirebaseUser, name: String, randomCodeForTodoChat: String) {
        val firebaseService = FirebaseService()

        firebase.setUserState(
            UserModel(
                mutableListOf(randomCodeForTodoChat),
                user.email.toString(),
                name,
                ""
            ),
            user.uid
        )

        firebaseService.setChat(ChatModel(participants = mutableListOf(user.uid),
            massages = mutableListOf(
                MessageModel(user.uid, "Hi I am your todo chat", "", time = DataTimeHelper().getIsoUtcFormat())),
            typeOfChat = TYPE_TO_DO, lastMassage = " ", lastTime = DataTimeHelper().getIsoUtcFormat()), chatId = randomCodeForTodoChat)
    }


    private fun registerUserWithData(){
        binding.progressBar.visibility = View.VISIBLE

        val email = binding.edGmail.text.toString()
        val password = binding.edPassword.text.toString()
        val name = binding.edName.text.toString()

        firebase.getAllUsers {
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter Gmail", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
            else if (name.isEmpty() || it.contains(name.trimEnd())) {
                Toast.makeText(this, "Enter Name or change other name", Toast.LENGTH_SHORT)
                    .show()
                binding.progressBar.visibility = View.GONE
            }
            else if (reg)
                register(email, password, name)
            else
                auth(email, password)
        }
    }

    private fun autoReg(){
        data class RegModel (
            val name: String,
            val email: String,
            val password: String
        )

        FirebaseAuth.getInstance().signOut()

        val usersReg: ArrayList<RegModel> = arrayListOf(
            RegModel("user1","denys54533@gmail.com", "1111111111"),
            RegModel("user2","denys545339@gmail.com", "1111111111"),
            RegModel("user3","brawlstarsden09@gmail.com", "1111111111"),
            RegModel("user4","kriptoden545@gmail.com", "1111111111"),
            RegModel("user5","instagramems54@gmail.com", "1111111111"),
        )

        var i: Int =1
        usersReg.forEach {
            register2(it.email, it.password, it.name){
                    FirebaseAuth.getInstance().signOut()
            }
        }
    }

    private fun register2(email: String, password: String, name: String, callback: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE

                    setRegisterUserStateInBD(
                        auth.currentUser!!,
                        name.trimEnd(),
                        (1000..9000).random().toString()
                    )
//                    val intent = Intent(this, ScrollActivity::class.java)
//                    startActivity(intent)
                    Toast.makeText(this, "add $name", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                callback(task.isSuccessful)
            }
    }
}