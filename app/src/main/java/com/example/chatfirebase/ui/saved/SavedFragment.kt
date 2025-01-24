package com.example.chatfirebase.ui.saved

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatfirebase.R
import com.example.chatfirebase.RcMassage
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.databinding.FragmentChatBinding
import com.example.chatfirebase.databinding.FragmentSavedBinding
import com.example.chatfirebase.ui.chat.ChatViewModel

class SavedFragment : Fragment() {

    companion object {
        fun newInstance() = SavedFragment()
    }

    private val viewModel: ChatViewModel by viewModels()
    val adapter = RcMassage()
    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

        viewModel.listChat.observe(viewLifecycleOwner) { list ->
            Log.d("ooo", "null? list $list")
            adapter.setList(list)
            binding.rcView.layoutManager = LinearLayoutManager(requireContext())
            binding.rcView.adapter = adapter
            binding.rcView.scrollToPosition(adapter.itemCount - 1)
        }


        binding.bSend.setOnClickListener {
            val massage = binding.editTextText.text.toString()
            binding.editTextText.text.clear()
            viewModel.sentMassage(massage = massage, chatId = chatId, typeOfChat = TYPE_TO_DO, userCode = clickedUser)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }
}