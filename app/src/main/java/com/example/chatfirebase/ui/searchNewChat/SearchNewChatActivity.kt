package com.example.chatfirebase.ui.searchNewChat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatfirebase.AdapterNewChat
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.ui.chat.ChatActivity
import com.google.firebase.auth.FirebaseAuth

class SearchNewChatActivity : AppCompatActivity(),  AdapterNewChat.OnItemClickListener{
    val firebaseService = FirebaseService()
    val uid = FirebaseAuth.getInstance().uid
     var adapterUsers =  AdapterNewChat(this)
    private var list: ArrayList<UserModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_new_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initSearchView()
        val back = this.findViewById<ImageView>(R.id.buttonBackSearchnew)
        back.setOnClickListener { finish() }

        val rcView = this.findViewById<RecyclerView>(R.id.rcNevChat)
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapterUsers
    }

    private fun initSearchView(){
        val searchView = this.findViewById<SearchView>(R.id.searchNewChat)
        searchView.requestFocus()

        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchUsers(newText!!)
                return true
            }
        })
    }

    fun searchUsers(text: String) {
        var listUsersForFilter:HashMap<String, UserModel> = hashMapOf()
        if (text.isNotEmpty()) {
            firebaseService.getAllUsers { allUsers ->
                firebaseService.getUser(uid!!) { me ->
                    Log.d("ooo", "me chats ${me.chats}")

                    if (!me.chats.isNullOrEmpty()) {
                        val listUsers = getUsersWithNotChat(me, allUsers)
                        listUsersForFilter = listUsers.filter {
                            it.value.name.contains(
                                text,
                                ignoreCase = true
                            )
                        } as HashMap<String, UserModel>
                        val userModels = listUsersForFilter
                        adapterUsers.updateAdapter(userModels)
                        Log.d("ooo", "listFilteredUuUUU1111 $listUsersForFilter")
                    } else {
                        firebaseService.getAllUsers { allUsers ->
                            listUsersForFilter = allUsers.filter {
                                it.value.name.contains(
                                    text,
                                    ignoreCase = true
                                )
                            } as HashMap<String, UserModel>
                            Log.d("ooo", "listFilteredUuUUU222 $listUsersForFilter")
                            val userModels = listUsersForFilter
                            adapterUsers.updateAdapter(userModels)
                        }
                    }
                }
            }
        }
        Log.d("ooo", "listFilteredUuUUU $listUsersForFilter")
    }

    private fun getUsersWithNotChat(me: UserModel,  allUsers: HashMap<String, UserModel>) : HashMap<String, UserModel>{
        val listUsersForFilter: HashMap<String, UserModel> = hashMapOf()

            for (user in allUsers){
//                val hasCommonElements = me.chats!!.any { it in user.value.chats!! }
//                if (!hasCommonElements){
//                    listUsersForFilter[user.key] = user.value
//                }
            }
        allUsers.remove(uid)
        return allUsers
    }

    override fun onItemClick(position: Int) {
        val item = adapterUsers.getList()[position]
        Log.d("ooo", "item ===== $item")
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("clickedUser", item.keys.first())
        startActivity(intent)
        finish()
    }
}