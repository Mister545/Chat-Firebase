package com.example.chatfirebase

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.RcView.ModelUserRv
import com.example.chatfirebase.databinding.ItemAddNewUserBinding
import com.example.chatfirebase.databinding.ItemChatBinding
import java.util.ArrayList

    class AdapterNewChat(val listener: OnItemClickListener) : RecyclerView.Adapter<AdapterNewChat.AdapterNewChatHolder>() {

        private var listUsers = ArrayList<UserModel>()

        interface OnItemClickListener {
            fun onItemClick(position: Int)
        }


        inner class AdapterNewChatHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding = ItemAddNewUserBinding.bind(view)

            fun bind(user: UserModel) {

                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition)
                }
                binding.apply {
                    var image =
                        "https://cdn-icons-png.flaticon.com/512/3177/3177440.png"
                    if (user.image == "") {
                        Glide.with(itemView.context)
                            .load(image)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imageView)
                    } else {
                        Log.d("ooo", "image ========== $image")

                        image = user.image!!
                        Glide.with(itemView.context)
                            .load(image)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imageView)
                    }
                    binding.tvName.text = user.name
                }
            }
        }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): AdapterNewChat.AdapterNewChatHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_new_user, parent, false)
                return AdapterNewChatHolder(view)
            }

            override fun onBindViewHolder(
                holder: AdapterNewChat.AdapterNewChatHolder,
                position: Int
            ) {
                holder.bind(listUsers[position])
            }

            override fun getItemCount(): Int {
                return listUsers.size
            }

            fun setList(list: ArrayList<UserModel>) {
                listUsers = list
            }

            @SuppressLint("NotifyDataSetChanged")
            fun updateAdapter(listItems: MutableList<UserModel>) {
                listUsers.clear()
                listUsers.addAll(listItems)
                notifyDataSetChanged()
            }
        }
