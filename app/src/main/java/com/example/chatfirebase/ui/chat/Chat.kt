package com.example.chatfirebase.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.RcMassage
import com.example.chatfirebase.databinding.FragmentChatBinding

class Chat : Fragment() {

    companion object {
        fun newInstance() = Chat()
    }

    private val viewModel: ChatViewModel by viewModels()
    val adapter = RcMassage()
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chatId = arguments?.getString("clickedItem") // Отримуємо значення
        val clickedUser = arguments?.getString("clickedUser") // Отримуємо значення

        Log.d("ooo", "argument in chat $chatId")
        viewModel.start(chatId = chatId, clickedUser = clickedUser)

        viewModel.partner.observe(requireActivity()){
            Log.d("ooo", "pertner $it")
            val toolbarImage = requireActivity().findViewById<ImageView>(R.id.iconUser)
            val toolbarName = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
            if (it.image.isEmpty()){
                toolbarName.text = it.name
                Glide.with(requireContext())
                    .load(R.drawable.user_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(toolbarImage)
            } else {
                toolbarName.text = it.name
                Glide.with(requireContext())
                    .load(it.image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(toolbarImage)
            }
        }
        viewModel.listChat.observe(requireActivity()){
            Log.d("ooo", "null? list ${it}")
            adapter.setList(it)
            binding.rcView.layoutManager = LinearLayoutManager(requireContext())
            binding.rcView.adapter = adapter
            binding.rcView.scrollToPosition(adapter.itemCount - 1)
        }

        binding.bSend.setOnClickListener {
            val massage = binding.editTextText.text.toString()
            binding.editTextText.text.clear()
            viewModel.sentMassage(massage, chatId, clickedUser)
        }
    }
}