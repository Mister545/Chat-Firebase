package com.example.chatfirebase.ui.newGroup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.FragmentNewGroupBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewGroupFragment : Fragment(), NewGroupAdapter.OnItemClickListener {

    private val viewModel: NewGroupViewModel by viewModels()
    private val adapter = NewGroupAdapter(this)

    private var _binding: FragmentNewGroupBinding? = null
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rcNewGrope.layoutManager = LinearLayoutManager(requireContext())
        binding.rcNewGrope.adapter = adapter

        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.setList(users)
        }

        binding.imGroup.setOnClickListener { selectGroupImage() }

        viewModel.imageUri.observe(viewLifecycleOwner){
            Glide.with(this)
                .load(it)
                .error(R.drawable.user_default)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imGroup)
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.fabNewChat).setOnClickListener {
            viewModel.makeGroup(
                nameGroup = binding.nameOfGroup.text.toString(),
                selectedUsers = adapter.getListSelected()
            )
        }
    }

    private fun selectGroupImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultLauncher.launch(intent)
    }

    override fun onItemClick(position: Int) {
        Log.d("ooo", "clicked position: $position")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.destroy()
        _binding = null
    }
}
