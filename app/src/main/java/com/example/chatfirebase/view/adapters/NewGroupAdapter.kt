package com.example.chatfirebase.view.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.data.User
import com.example.chatfirebase.databinding.ItemAddNewUserBinding
import java.util.ArrayList

class NewGroupAdapter(val listener: OnItemClickListener) :
    RecyclerView.Adapter<NewGroupAdapter.AdapterNewChatHolder>() {

        private var listUsers = HashMap<String, User>()
        private var listSelected = HashMap<String, User>()

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }


        inner class AdapterNewChatHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding = ItemAddNewUserBinding.bind(view)

            @SuppressLint("ResourceAsColor")
            fun bind(user: User, modelKey : String) {

                itemView.setOnClickListener {
                    if (listSelected.containsKey(modelKey)){
                        binding.imageView2.visibility = View.GONE
                        listSelected.remove(modelKey)
                    }else{
                        binding.imageView2.visibility = View.VISIBLE
                        listSelected[modelKey] = user
                    }
                    Log.d("ooo", "list selected $listSelected")
                    listener.onItemClick(absoluteAdapterPosition)
                }

                binding.apply {
                        Glide.with(itemView.context)
                            .load(user.image)
                            .error(R.drawable.user_default)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imageView)
                    binding.tvName.text = user.name
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AdapterNewChatHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_new_user, parent, false)
            return AdapterNewChatHolder(view)
        }

        override fun onBindViewHolder(
            holder: AdapterNewChatHolder,
            position: Int
        ) {
            val userList = listUsers.toList() // Створює список пар (key-value)
            val (key, value) = userList[position] // Отримуємо пару за позицією
            holder.bind(value, key) // Передаємо значення у bind
        }

        override fun getItemCount(): Int {
            return listUsers.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setList(list: HashMap<String, User>) {
            listUsers = list
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun updateAdapter(listItems: HashMap<String, User>) {
            listUsers.clear()
            listUsers = listItems
            notifyDataSetChanged()
        }

        fun getList(): ArrayList<HashMap<String, User>> {
            val arrayList = ArrayList<HashMap<String, User>>()
            for ((key, value) in listUsers) {
                val singleEntryMap = hashMapOf(key to value)
                arrayList.add(singleEntryMap)
            }
            return arrayList
        }

        fun getListSelected(): HashMap<String, User>  {
            return listSelected
        }
    }
