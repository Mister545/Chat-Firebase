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
import com.example.chatfirebase.ui.addNewGrope.AddNewGrope
import com.example.chatfirebase.ui.searchNewChat.SearchNewChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
                else -> fabNewChat.visibility = View.GONE
            }
        }
        fabNewChat.setOnClickListener {
            val intent = Intent(this, SearchNewChatActivity::class.java)
            startActivity(intent)
//            addNewChatListener()
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
}