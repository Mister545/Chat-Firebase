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
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.TYPE_OF_CHAT
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
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
        val type = arguments?.getString(TYPE_OF_CHAT) // Отримуємо значення

        Log.d("ooo", "is to do in chat frag $type")
        viewModel.start(chatId = chatId, clickedUser = clickedUser)

        viewModel.listChat.observe(requireActivity()) {
            Log.d("ooo", "null? list ${it}")
            adapter.setList(it)
            binding.rcView.layoutManager = LinearLayoutManager(requireContext())
            binding.rcView.adapter = adapter
            binding.rcView.scrollToPosition(adapter.itemCount - 1)
        }

        binding.bSend.setOnClickListener {
            val massage = binding.editTextText.text.toString()
            binding.editTextText.text.clear()
            viewModel.sentMassage(massage = massage, chatId = chatId, typeOfChat = type, userCode = clickedUser)
        }

        initToolbar(type)
        viewModel.group.observe(requireActivity()){
            initToolbar(it.typeOfChat)
        }
        viewModel.group.observe(requireActivity()){
            initToolbar(it.typeOfChat)
        }
    }

    private fun initToolbar(type: String?) {
        when (type) {
            TYPE_TO_DO -> {
                initToDoToolbar()
            }
            TYPE_GROUP -> {
                initGropeToolbar()
            }
            TYPE_CHAT -> {
                initChatToolbar()
            }else -> {
                initChatToolbar()
            }
        }
    }

    private fun initChatToolbar() {
        viewModel.partner.observe(requireActivity()) {
            Log.d("ooo", "pertner $it")
            val toolbarImage = requireActivity().findViewById<ImageView>(R.id.iconUser)
            val toolbarName = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
            if (it.image.isEmpty()) {
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

    private fun initGropeToolbar() {
        viewModel.group.observe(requireActivity()) {
            Log.d("ooo", "pertner $it")
            val toolbarImage = requireActivity().findViewById<ImageView>(R.id.iconUser)
            val toolbarName = requireActivity().findViewById<TextView>(R.id.toolbarTitle)
            if (it.imageOfGroup.isNullOrEmpty()) {
                toolbarName.text = it.nameOfGroup
                Glide.with(requireContext())
                    .load(R.drawable.user_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(toolbarImage)
            } else {
                toolbarName.text = it.nameOfGroup
                Glide.with(requireContext())
                    .load(it.imageOfGroup)
                    .apply(RequestOptions.circleCropTransform())
                    .into(toolbarImage)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }
}