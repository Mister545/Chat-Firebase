package com.example.chatfirebase.ui.chat

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.MessageModel
import com.example.chatfirebase.R
import com.example.chatfirebase.RcMassage
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.TYPE_OF_CHAT
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.databinding.FragmentChatBinding
import com.example.chatfirebase.ui.ChatState

class ChatFragment : Fragment() {

    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory(FirebaseService())
        )[ChatViewModel::class.java]
    }
    val adapter = RcMassage()
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

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
        val type = arguments?.getString(TYPE_OF_CHAT) // Отримуємо значення

        Log.d("ooo", "is to do in chat frag $type")
        viewModel.start(chatId = chatId, clickedUser = clickedUser)

        viewModel.state.observe(viewLifecycleOwner) {
            initRcView(it.messages)
        }

        binding.bSend.setOnClickListener {
            val message = binding.editTextText.text.toString()
            binding.editTextText.text.clear()
            viewModel.sendMessage(
                messageText = message,
                chatId = chatId!!
            )
        }
    }

    private fun initRcView(list: List<MessageModel>) {
        Log.d("ooo", "null? list ${list}")
        adapter.setList(list)
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.adapter = adapter
        binding.rcView.scrollToPosition(adapter.itemCount - 1)
    }

    private fun initToolbar(type: String?, state: ChatState) {
        when (type) {
            TYPE_TO_DO -> {
                initToDoToolbar()
            }

            TYPE_GROUP -> {
                initGropeToolbar(state.chatType!!)
            }

            TYPE_CHAT -> {
                initChatToolbar(state.partner!!)
            }

            else -> {
                initChatToolbar(state.partner)
            }
        }
    }

    private fun initChatToolbar(partner: UserModel?) {

        if (partner != null) {
            Log.d("ooo", "pertner $partner")
            val toolbarImage = requireActivity().findViewById<ImageView>(R.id.iconUser)
            val toolbarName = requireActivity().findViewById<TextView>(R.id.toolbarTitle)

            toolbarName.text = partner.name
            Glide.with(requireContext())
                .load(partner.image)
                .error(R.drawable.user_default)
                .apply(RequestOptions.circleCropTransform())
                .into(toolbarImage)
        }
    }

    private fun initToDoToolbar() {
        val toolbarImage = requireActivity().findViewById<ImageView>(R.id.iconUser)
        val toolbarName = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
        val isOnline = requireActivity().findViewById<TextView>(R.id.toolbarOnline)
        isOnline.text = " "
        toolbarName.text = "Saved"
        Glide.with(requireContext())
            .load(R.drawable.bookmark_24px)
            .apply(RequestOptions.circleCropTransform())
            .into(toolbarImage)
    }

    private fun initGropeToolbar(chat: ChatModel) {
        Log.d("ooo", "pertner $chat")
        val toolbarImage = requireActivity().findViewById<ImageView>(R.id.iconUser)
        val toolbarName = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
        toolbarName.text = chat.nameOfGroup
        Glide.with(requireContext())
            .load(chat.imageOfGroup)
            .error(R.drawable.group_ic)
            .apply(RequestOptions.circleCropTransform())
            .into(toolbarImage)
    }
}