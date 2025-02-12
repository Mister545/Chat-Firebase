package com.example.chatfirebase.ui.settings

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.DialogHelper
import com.example.chatfirebase.LocaleHelper
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.FragmentSettingsBinding
import com.example.chatfirebase.ui.registration.SignInAct

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

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

        // Ініціалізація SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("ThemePrefs", MODE_PRIVATE)

        Log.d("ooo", "isDark mode : ${sharedPreferences.getBoolean("isDarkMode", false)}")
        initThem()


        binding.switchTheme.setOnClickListener {
            switchTheme()
        }

        binding.language.setOnClickListener {
            DialogHelper.showLanguageDialog(requireActivity())
        }

        binding.tvNameLanguage.text = LocaleHelper.getSavedLanguage(requireContext())

        binding.accountExit.setOnClickListener {
            viewModel.exitAccount()
            val intent = Intent(requireContext(), SignInAct::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        viewModel.user.observe(viewLifecycleOwner){
            _binding?.tvName?.text = it.name

            val context = context ?: return@observe // Перевірка перед використанням

            Glide.with(context)
                .load(it.image)
                .error(R.drawable.user_default)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imUser)
        }
    }

    private fun initThem(){
        if (sharedPreferences.getBoolean("isDarkMode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.switchTheme.isChecked = false
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.switchTheme.isChecked = true
        }
    }

    private fun switchTheme(){
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)
        val editor = sharedPreferences.edit()

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            editor.putBoolean("isDarkMode", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            editor.putBoolean("isDarkMode", true)
        }

        editor.apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
