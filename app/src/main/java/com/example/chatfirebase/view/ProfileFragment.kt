package com.example.chatfirebase.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.FragmentProfileBinding
import com.example.chatfirebase.viewModel.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.uploadImageUri(uri)
                    Log.d("ooo", "Selected image URI: $uri")
                }
            }
        }

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Додавання меню
        setHasOptionsMenu(true)

        // Зміна кольору статус-бару
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.p_toolbar_nickname)

        viewModel.init()

        binding.setImagePrifileFAB.setOnClickListener {
            selectProfileImage()
        }

        viewModel.user.observe(viewLifecycleOwner) {
            _binding?.let { binding ->
                binding.nameT.text = it.name
                binding.email.text = it.email
                binding.intoduseUser.text =
                    if (it.introduceYourSelf.isNullOrEmpty()) "My name is ${it.name}" else it.name
                binding.dateOfBorn.text =
                    if (it.dateOfBerth == "0.0.0000") "--.--.----" else it.dateOfBerth
                Glide.with(requireContext())
                    .load(it.image)
                    .error(R.drawable.user_default)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imUserProfile)
            }
        }
    }

    private fun selectProfileImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultLauncher.launch(intent)
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
                Toast.makeText(requireContext(), "Налаштування натиснуто", Toast.LENGTH_SHORT)
                    .show()
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
