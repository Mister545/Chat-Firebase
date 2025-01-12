package com.example.chatfirebase.ui.searchNewChat

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatfirebase.AdapterNewChat
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random

class SearchNewChatActivity : AppCompatActivity(),  AdapterNewChat. OnItemClickListener{
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
        val rcView = this.findViewById<RecyclerView>(R.id.rcNevChat)
        rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = adapterUsers
    }

    private fun initSearchView(){
        val searchView = this.findViewById<SearchView>(R.id.searchNewChat)

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
                val hasCommonElements = me.chats!!.any { it in user.value.chats!! }
                if (!hasCommonElements){
                    listUsersForFilter[user.key] = user.value
                }
            }
        return listUsersForFilter
    }
    private fun getMyChatsUsers(callback: (Map<String, UserModel>) -> Unit) {
        val myChats = mutableListOf<String>()

        firebaseService.getAllUsers { allUsersHashMap ->
            firebaseService.getAllChats { _, mapChats ->
                mapChats.forEach { chat ->
                    if (chat.value.participants.contains(uid)) {
                        myChats.addAll(chat.value.participants)
                    }
                    val myChatsClean = myChats.filterNot { it == uid }
                    Log.d("ooo", "my chats =============== $myChatsClean")
                    val myChatsUsers = allUsersHashMap.filter { it.key in myChatsClean }
                    Log.d("ooo", "u =============== $myChatsUsers")
                    callback(myChatsUsers)
                }
            }
        }
    }

    override fun onItemClick(position: Int) {
        val item = adapterUsers.getList()[position]
        Log.d("ooo", "item ===== $item")
        addNewChat(item)
        finish()
    }

    private fun addNewChat(item: HashMap<String, UserModel>) {
        val randomCode = (100000..999999).random().toString()
        val chatModel = ChatModel(
            participants = mutableListOf(uid!!, item.keys.first()),
            massages = mutableListOf(),
            lastMassage = "",
            lastTime = ""
        )
        firebaseService.setChat(chatModel, randomCode)

        firebaseService.getUser(uid){
            val arr = mutableListOf<String>()  // Список
            val chatsToAdd: MutableList<String> = it.chats ?: mutableListOf()
            arr.addAll(chatsToAdd)

                firebaseService.setUserState(userModel =
                UserModel(
                arr,
                ),uid)
        }
        firebaseService.getUser(item.keys.first()){
            val arr = mutableListOf<String>()  // Список
            val chatsToAdd: MutableList<String> = it.chats ?: mutableListOf()

            arr.addAll(chatsToAdd)

            firebaseService.setUserState(userModel =
            UserModel(
                arr,
            ),item.keys.first())
        }
    }
}