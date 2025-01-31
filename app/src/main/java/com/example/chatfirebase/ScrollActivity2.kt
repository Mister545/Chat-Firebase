package com.example.chatfirebase

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chatfirebase.databinding.ActivityScroll2Binding
import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.ui.addNewGrope.AddNewGrope
import com.example.chatfirebase.ui.chat.ChatActivity
import com.example.chatfirebase.ui.registration.SignInAct
import com.example.chatfirebase.ui.saved.SavedFragment
import com.example.chatfirebase.ui.searchNewChat.SearchNewChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class ScrollActivity2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityScroll2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScroll2Binding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.appBarScroll2.toolbar)

        window.statusBarColor = ContextCompat.getColor(this, R.color.nav_header_background)

        binding.appBarScroll2.fabNewChat.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fabNewChat).show()
        }

        setDataOnNavHeader()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_scroll2)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_profile,
                R.id.nav_new_group,
                R.id.nav_saved,
                R.id.nav_settings,
                R.id.nav_chat
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val fabNewChat = findViewById<FloatingActionButton>(R.id.fabNewChat)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.nav_chat -> {
                    fabNewChat.visibility = View.VISIBLE
                    fabNewChat.setImageResource(R.drawable.baseline_add_24)
                }
                R.id.nav_new_group -> {
                    fabNewChat.visibility = View.VISIBLE
                    fabNewChat.setImageResource(R.drawable.arrow_forward_24px)
                }
                R.id.nav_saved -> {
                    fabNewChat.visibility = View.GONE
                }
                else -> fabNewChat.visibility = View.GONE
            }
        }
        fabNewChat.setOnClickListener {
            val intent = Intent(this, SearchNewChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun clickOnSavedListener(){
        FirebaseService().getUser(FirebaseAuth.getInstance().uid!!){
            val codeSaved = it.chats!!.filter { it.length == 4 }
            val fragment = SavedFragment()
            val bundle = Bundle()
            bundle.putString("clickedItem", codeSaved[0])
            bundle.putBoolean("isToDo", true)
            fragment.arguments = bundle
        }
    }

    fun addNewChatListener(){
        val intent = Intent(this, AddNewGrope::class.java)
        startActivity(intent)
    }

    fun addNewGropeListener(){
        val intent = Intent(this, AddNewGrope::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.scroll_activity2, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_scroll2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setDataOnNavHeader(){
        val firebaseService = FirebaseService()
        val uid = FirebaseAuth.getInstance().uid
        firebaseService.getUser(uid!!){
            val name = findViewById<TextView>(R.id.nameNav)
            val email = findViewById<TextView>(R.id.gmailNav)
            val image = findViewById<ImageView>(R.id.imageView)

            name.text = it.name
            email.text = it.email
            if (it.image.isNotEmpty()) {
                Glide.with(this)
                    .load(it.image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(image)
            } else {
                image.setImageResource(R.drawable.user_default)
            }
        }
    }
}