package com.example.chatfirebase.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.chatfirebase.ui.editProfile.EditProfile
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Налаштування Toolbar
        val toolbar: Toolbar = binding.toolbar123
        toolbar.title = "Data profile"

        // Додавання меню
        setHasOptionsMenu(true)

        // Зміна кольору статус-бару
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.p_toolbar_nickname)

        // Обробка кнопки "Назад"
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val intent = Intent(requireContext(), EditProfile::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                Toast.makeText(requireContext(), "Налаштування натиснуто", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
