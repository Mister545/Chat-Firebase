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
import com.example.chatfirebase.MainActivity
import com.example.chatfirebase.RcView.ChatAdapter
import com.example.chatfirebase.RcView.ModelChat
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.databinding.FragmentChatsBinding
import com.example.chatfirebase.ui.chat.ChatActivity
import com.example.chatfirebase.ui.registration.SignInAct
import com.google.firebase.auth.FirebaseAuth

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

        viewModel.getListChatsFromBd {
            Log.d("ooo", it.toString())
            Log.d("ooo", "")
        }
        viewModel.chatsInit()

        viewModel.listChats.observe(requireActivity()) {
            adapterChat.setListChats(it, viewModel.me.value!!.name)
        }

        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.adapter = adapterChat
    }

    override fun onItemClick(position: Int) {
        val clickedItem = adapterChat.getList()[position]
        val intent = Intent(requireContext(), ChatActivity::class.java)
        intent.putExtra("clickedItem", clickedItem.codeChat)
        startActivity(intent)
    }

    private fun authCheck() {
        if (viewModel.authInit()) {
            return
        } else {
            val intent = Intent(requireContext(), SignInAct::class.java)
            startActivity(intent)
        }
    }
}



