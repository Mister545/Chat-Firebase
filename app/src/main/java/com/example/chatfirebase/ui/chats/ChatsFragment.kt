package com.example.chatfirebase.ui.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatfirebase.RcView.ModelUserRv
import com.example.chatfirebase.databinding.FragmentChatsBinding

class ChatsFragment : Fragment() {


    companion object {
        fun newInstance() = ChatsFragment()
    }

    private val viewModel: ChatsViewModel by viewModels()

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
    }
}



