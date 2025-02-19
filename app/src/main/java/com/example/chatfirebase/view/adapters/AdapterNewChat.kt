package com.example.chatfirebase.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.data.User
import com.example.chatfirebase.databinding.ItemAddNewUserBinding

class AdapterNewChat(val listener: OnItemClickListener) :
    RecyclerView.Adapter<AdapterNewChat.AdapterNewChatHolder>() {

    private var listUsers = HashMap<String, User>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }


    inner class AdapterNewChatHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemAddNewUserBinding.bind(view)

        @SuppressLint("ResourceAsColor")
        fun bind(user: User) {

            itemView.setOnClickListener {
                itemView.setBackgroundColor(R.color.date_picker_button)
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
        val userModels = listUsers.map { it.value }
        holder.bind(userModels[position])
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
}
