package com.example.chatfirebase.RcView

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.databinding.ItemChatBinding


class ViewAdapter(val listener: OnItemClickListener) : RecyclerView.Adapter<ViewAdapter.ScrollHolder>(){

    var meName = ""

    private var listMassages = mutableListOf<ModelUserRv>()
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    inner class ScrollHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemChatBinding.bind(view)

        fun bind(userRv : ModelUserRv){

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            binding.apply {
                val user = userRv.names.filter { it.name != meName }
                tvName.text = user[0].name
                var image =
                    "https://cdn-icons-png.flaticon.com/512/3177/3177440.png"
                if (user[0].image == "") {
                    Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imIcUser)
                }else{
                    image = user[0].image
                    Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imIcUser)
                }
                tvLastTime.text = userRv.lastTime
                tvLastMassage.text = userRv.lastMassage
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAdapter.ScrollHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ScrollHolder(view)
    }

    override fun onBindViewHolder(holder: ViewAdapter.ScrollHolder, position: Int) {
        holder.bind(listMassages[position])
    }

    override fun getItemCount(): Int {
        return listMassages.size
    }

    @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
    fun setListMassages(list: MutableList<ModelUserRv>, userUid: String?){
        listMassages = list
        if (userUid != null)
        meName = userUid

        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems: List<ModelUserRv>){
        Log.d("ooo", "listItems $listItems")

        listMassages.clear()
        listMassages.addAll(listItems)
        notifyDataSetChanged()
    }

    fun getList(): MutableList<ModelUserRv>{
        return listMassages
    }

}
