package com.example.chatfirebase.RcView

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.R
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.databinding.ItemChatBinding


class ChatAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<ChatAdapter.ScrollHolder>(){

    private var meName = ""

    private var listMassages = mutableListOf<ModelChat>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ScrollHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemChatBinding.bind(view)

        @SuppressLint("NewApi")
        fun bind(userRv : ModelChat){

            itemView.setOnClickListener {
                listener.onItemClick(absoluteAdapterPosition)
            }

            binding.apply {
                Log.d("ooo", "typ ${userRv.typeOfChat}")

                when (userRv.typeOfChat) {
                    TYPE_TO_DO -> {
                        chatTodo(userRv, binding, itemView)
                    }
                    TYPE_CHAT -> {
                        chatBetweenTwo(userRv, binding, itemView)
                    }
                    TYPE_GROUP -> {
                        chatGrope(userRv, binding, itemView)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ScrollHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ScrollHolder(view)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ScrollHolder, position: Int) {
        holder.bind(listMassages[position])
    }

    override fun getItemCount(): Int {
        return listMassages.size
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    fun setListChats(list: MutableList<ModelChat>, userUid: String?){
        listMassages = list.filterNot { it.names.size == 1 }.toMutableList()  //// without todo list
        if (userUid != null)
            meName = userUid

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(list: List<ModelChat>, userUid: String?) {
        Log.d("ooo", "listItems $list")

        // Якщо userUid не дорівнює null, оновлюємо meName
        userUid?.let { meName = it }

        // Додаємо тільки ті елементи, яких ще немає в listMassages
        val newItems = list.filter { newItem ->
            listMassages.none { existingItem -> existingItem.codeChat == newItem.codeChat }
        }

//        listMassages = newItems.filter { it.typeOfChat == TYPE_TO_DO }.toMutableList()  //// without todo list

        listMassages.addAll(newItems) // Додаємо лише нові елементи
        Log.d("ooo", "New items added: ${newItems.size}, Total size: ${listMassages.size}")

        notifyDataSetChanged() // Оновлюємо адаптер
    }



    fun getList(): MutableList<ModelChat>{
        return listMassages
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chatBetweenTwo(userRv: ModelChat, binding: ItemChatBinding, itemView: View){
        binding.apply {
            val user = userRv.names.filter { it.name != meName }
            tvName.text = user[0].name

            tvLastTime.text = DataTimeHelper().convertIsoUtcFToLocal(userRv.lastTime)
            tvLastMassage.text = userRv.lastMassage

            Glide.with(itemView.context)
                .load(user[0].image)
                .error(R.drawable.user_default)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imIcUser)
        }
    }

    private fun chatTodo(userRv: ModelChat, binding: ItemChatBinding, itemView: View){
        binding.apply {
            tvName.text = "Saved"

            tvLastTime.text = ""
            tvLastMassage.text = ""
            Glide.with(itemView.context)
                .load(R.drawable.bookmark_24px)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imIcUser)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chatGrope(userRv: ModelChat, binding: ItemChatBinding, itemView: View){
        binding.apply {
            tvName.text = userRv.nameOfGroup

            Log.d("ooo", "name group ${userRv.nameOfGroup}")
            tvLastTime.text = DataTimeHelper().convertIsoUtcFToLocal(userRv.lastTime)
            tvLastMassage.text = if(userRv.lastMassage.isEmpty()) "You was joined in group" else "" +
                    userRv.lastMassage

            Glide.with(itemView.context)
                .load(userRv.imageOfGroup)
                .error(R.drawable.group_ic)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imIcUser)
        }
    }

}
