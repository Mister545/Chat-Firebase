package com.example.chatfirebase.ui.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatfirebase.DialogHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.RcMassage
import com.example.chatfirebase.databinding.FragmentSavedBinding
import com.example.chatfirebase.ui.chat.ChatViewModel
import com.example.chatfirebase.ui.chat.ChatViewModelFactory

class SavedFragment : Fragment() {

    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory(FirebaseService())
        )[ChatViewModel::class.java]
    }
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatId = arguments?.getString("clickedItem") ?: ""
        val clickedUser = arguments?.getString("clickedUser") ?: ""


        viewModel.start(chatId = chatId, clickedUser = clickedUser)

        Log.d("ooo", "data saved $chatId, $clickedUser")

        viewModel.state.observe(viewLifecycleOwner) {
            Log.d("ooo", "null? list ${it.messages}")
            val adapter = RcMassage(
                onEdit = {message ->
                    DialogHelper.showInputDialog(requireContext(), message.second.message!!){
                        viewModel.editMessage(chatId = chatId, newText = it, messageId = message.first)
                    }
                },
                onDelete = {message ->
                    viewModel.deleteMessage(chatId = chatId, messageId = message.first)
                }
            )
            adapter.setList(it.messages)
            binding.rcView.layoutManager = LinearLayoutManager(requireContext())
            binding.rcView.adapter = adapter
            binding.rcView.scrollToPosition(adapter.itemCount - 1)
        }


        binding.bSend.setOnClickListener {
            val message = binding.editTextText.text.toString()
            binding.editTextText.text.clear()
            viewModel.sendMessage(newUserId = "", messageText = message, chatId = chatId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}