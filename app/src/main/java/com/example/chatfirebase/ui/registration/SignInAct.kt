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
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.ScrollActivity
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
                    register(email, password)
                else
                    auth(email, password)
            }

        }
    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    setTodoChat((0..10000).random().toString())
                    binding.progressBar.visibility = View.GONE
                    setRegisterUserStateInBD(auth.currentUser!!, binding.edName.text.toString().trimEnd())
                    val intent = Intent(this, ScrollActivity::class.java)
                    startActivity(intent)
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

    private fun auth(email: String, password: String) {
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

    private fun setRegisterUserStateInBD(user: FirebaseUser, name: String) {
        firebase.setUserState(
            UserModel(
                mutableListOf((0..10000).random().toString()),
                user.email.toString(),
                name,
                ""
            ),
            user.uid
        )
    }

    private fun setTodoChat(randomCode: String){
        val uid = FirebaseAuth.getInstance().uid
        val firebaseService = FirebaseService()
        firebaseService.getUser(uid!!){
            firebaseService.setUserState(UserModel(mutableListOf(randomCode),
                it.email, it.name, it.image), uid)
        }
        firebaseService.setChat(ChatModel(participants = mutableListOf(uid)), randomCode)
    }
}