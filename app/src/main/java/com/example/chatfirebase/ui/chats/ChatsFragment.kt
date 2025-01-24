package com.example.chatfirebase.ui.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatfirebase.R
import com.example.chatfirebase.RcView.ChatAdapter
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.TYPE_OF_CHAT
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.databinding.FragmentChatsBinding
import com.example.chatfirebase.ui.chat.ChatActivity
import com.example.chatfirebase.ui.registration.SignInAct
import com.example.chatfirebase.ui.searchNewChat.SearchNewChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatsFragment : Fragment(), ChatAdapter.OnItemClickListener {


    companion object {
        fun newInstance() = ChatsFragment()
    }

    private val viewModel: ChatsViewModel by viewModels()

    private val adapterChat = ChatAdapter(this)
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authCheck()

        viewModel.chatsInit()

        viewModel.listChats.observe(requireActivity()) {
            Log.d("ooo", "ддддддддд ${it.size}")
            adapterChat.updateAdapter(it, viewModel.me.value!!.name)
        }

        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.adapter = adapterChat
    }

    override fun onResume() {
        super.onResume()
        val addNewChatFab = requireActivity().findViewById<FloatingActionButton>(R.id.fabNewChat)
        addNewChatFab.setOnClickListener {
            val intent = Intent(requireContext(), SearchNewChatActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onItemClick(position: Int) {
        val clickedItem = adapterChat.getList()[position]
        Log.d("ooo", "clickedItem.typeOfChat ${clickedItem.typeOfChat}")

        when (clickedItem.typeOfChat) {
            TYPE_TO_DO -> {
                Log.d("ooo", "Clicked item1 ${clickedItem.names.size}")
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra("clickedItem", clickedItem.codeChat)
                intent.putExtra(TYPE_OF_CHAT, TYPE_TO_DO)
                startActivity(intent)
            }
            TYPE_CHAT -> {
                Log.d("ooo", "Clicked item2 ${clickedItem.names.size}")
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra("clickedItem", clickedItem.codeChat)
                intent.putExtra(TYPE_OF_CHAT, TYPE_CHAT)
                startActivity(intent)
            }
            TYPE_GROUP -> {
                Log.d("ooo", "Clicked item3 ${clickedItem.names.size}")
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra("clickedItem", clickedItem.codeChat)
                intent.putExtra("isGroup", true)
                intent.putExtra(TYPE_OF_CHAT, TYPE_GROUP)
                startActivity(intent)
            }
        }
    }

    private fun authCheck() {
        if (viewModel.authInit()) {
            return
        } else {
            requireActivity().finish()
            val intent = Intent(requireContext(), SignInAct::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }
}



