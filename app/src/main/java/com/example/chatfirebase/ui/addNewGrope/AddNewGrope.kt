package com.example.chatfirebase.ui.addNewGrope

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.ActivityAddNewGropeBinding

class AddNewGrope : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewGropeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewGropeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar4.title = "New grope"
//        setSupportActionBar(binding.toolbar4)

        binding.fab2.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab2).show()
        }
    }
}