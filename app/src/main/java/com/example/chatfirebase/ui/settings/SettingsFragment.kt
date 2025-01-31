package com.example.chatfirebase.ui.settings

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.FragmentSettingsBinding
import com.example.chatfirebase.ui.registration.SignInAct

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountExit.setOnClickListener {
            viewModel.exitAccount()
            val intent = Intent(requireContext(), SignInAct::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        viewModel.user.observe(requireActivity()){
            binding.tvName.text = it.name

            Glide.with(requireContext())
                .load(it.image)
                .error(R.drawable.user_default)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imUser)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
