package com.example.chatfirebase.RcView

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.R
import com.example.chatfirebase.databinding.ItemChatBinding


class ChatAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<ChatAdapter.ScrollHolder>(){

    var meName = ""

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
                val user = userRv.names.filter { it.name != meName }
                tvName.text = user[0].name

                tvLastTime.text = DataTimeHelper().convertIsoUtcFToLocal(userRv.lastTime)
                tvLastMassage.text = userRv.lastMassage

                val image = user[0].image
                Glide.with(itemView.context)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imIcUser)
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
        listMassages = list
        if (userUid != null)
        meName = userUid

        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems: List<ModelChat>){
        Log.d("ooo", "listItems $listItems")

        listMassages.clear()
        listMassages.addAll(listItems)
        notifyDataSetChanged()
    }

    fun getList(): MutableList<ModelChat>{
        return listMassages
    }

}
