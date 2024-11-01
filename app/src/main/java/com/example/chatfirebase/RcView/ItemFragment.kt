package com.example.chatfirebase.RcView

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import com.example.chatfirebase.AdapterNewChat
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MainActivity
import com.example.chatfirebase.R
import com.example.chatfirebase.RcView.placeholder.PlaceholderContent
import com.example.chatfirebase.ScrollActivity
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.databinding.FragmentSearchUsersBinding
import com.example.chatfirebase.databinding.FragmentSearchUsersListBinding
import com.google.firebase.auth.FirebaseAuth

class ItemFragment : DialogFragment(), AdapterNewChat.OnItemClickListener {

    private lateinit var adapterUsers: AdapterNewChat
    private lateinit var list: ArrayList<UserModel>
    private lateinit var rcView: RecyclerView
    val firebaseService = FirebaseService()
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
        firebaseService.getAllUsers {
            for (userSnapshot in it) {

                if (userSnapshot.value == clickedItem){
                    val intent = Intent(context, ScrollActivity::class.java)
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

    private fun animationEmpty(list: ArrayList<UserModel>) {
        if (list.isEmpty())
            binding.animEmpty.visibility = View.VISIBLE
        else
            binding.animEmpty.visibility = View.GONE

    }

    fun searchUsers(text: String){

        if (text.isNotEmpty()) {
            firebaseService.getAllUsers { allUsers ->
                getMyChatsUsers { users ->
                    val usersAr: ArrayList<UserModel> = arrayListOf()

                    val notMyUsers = allUsers.filterNot { it.value in users.values }.filterNot { it.key == uid }

                    for (userName in notMyUsers) {
                        usersAr.add(userName.value)
                    }
                    val filteredUsers: ArrayList<UserModel> =
                        usersAr.filter { it.name.contains(text, ignoreCase = true) } as ArrayList<UserModel>

                    list = filteredUsers
                    animationEmpty(list)

                    adapterUsers.updateAdapter(list)

                    Log.d("ooo", "filteredUsers $filteredUsers")
                }
            }
        }
    }

    private fun getMyChatsUsers(callback: (Map<String, UserModel>) -> Unit){
        val myChats = mutableListOf<String>()

        firebaseService.getAllUsers { allUsersHashMap ->
            firebaseService.getAllChats{ _, mapChats ->
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
