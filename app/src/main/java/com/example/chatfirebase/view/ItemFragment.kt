package com.example.chatfirebase.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import com.example.chatfirebase.view.adapters.AdapterNewChat
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.R
import com.example.chatfirebase.data.User
import com.example.chatfirebase.databinding.FragmentSearchUsersListBinding
import com.google.firebase.auth.FirebaseAuth

class ItemFragment : DialogFragment(), AdapterNewChat.OnItemClickListener {

    private lateinit var adapterUsers: AdapterNewChat
    private lateinit var list: ArrayList<User>
    private lateinit var rcView: RecyclerView
    val firebaseRepositoryImpl = FirebaseRepositoryImpl()
    lateinit var binding: FragmentSearchUsersListBinding
    val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchUsersListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("ooo", "Started Item Activity")

        rcView = view.findViewById(R.id.rcViewUsers)

        initSearchView()
        adapterUsers = AdapterNewChat(this)
        rcView.layoutManager = LinearLayoutManager(context)
        rcView.adapter = adapterUsers
    }

    override fun onStart() {
        super.onStart()
        val searchStart = arguments?.getBoolean("searchActive") ?: false
        Log.d("ooo", "searchStart =============== $searchStart")

        if (searchStart){
            binding.searchView.isIconified = false

            binding.searchView.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onItemClick(position: Int) {
        val context = requireContext()
        val clickedItem = list[position]
        firebaseRepositoryImpl.getAllUsers {
            for (userSnapshot in it) {

                if (userSnapshot.value == clickedItem){
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("data", userSnapshot.key)
                    startActivity(intent)
                    dismiss()
                }else
                    dismiss()
            }
        }

    }

    private fun initSearchView(){

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
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

    private fun animationEmpty(list: ArrayList<User>) {
        if (list.isEmpty())
            binding.animEmpty.visibility = View.VISIBLE
        else
            binding.animEmpty.visibility = View.GONE

    }

    fun searchUsers(text: String){
        if (text.isNotEmpty()) {
            firebaseRepositoryImpl.getAllUsers { allUsers ->
                getMyChatsUsers { users ->
                    val usersAr: ArrayList<User> = arrayListOf()

                    val notMyUsers = allUsers.filterNot { it.value in users.values }.filterNot { it.key == uid }

                    for (userName in notMyUsers) {
                        usersAr.add(userName.value)
                    }
                    val filteredUsers: ArrayList<User> =
                        usersAr.filter { it.name.contains(text, ignoreCase = true) } as ArrayList<User>

                    list = filteredUsers
                    animationEmpty(list)

//                    adapterUsers.updateAdapter(list)

                    Log.d("ooo", "filteredUsers $filteredUsers")
                }
            }
        }
    }

    private fun getMyChatsUsers(callback: (Map<String, User>) -> Unit){
        val myChats = mutableListOf<String>()

        firebaseRepositoryImpl.getAllUsers { allUsersHashMap ->
            firebaseRepositoryImpl.getAllChats{ _, mapChats ->
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
}
