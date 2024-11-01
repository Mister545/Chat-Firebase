package com.example.chatfirebase

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.databinding.ItemMessageReceivedBinding
import com.example.chatfirebase.databinding.ItemMessageSentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class RcMassage : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messages = mutableListOf<MassageModel>()

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2
    val auth = Firebase.auth.currentUser

    class ReceivedMessageViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val binding = ItemMessageReceivedBinding.bind(view)

        fun bind(massageModel: MassageModel){
            binding.tvMassage.text = massageModel.massage
            binding.tvTime.text = massageModel.time
            binding.apply {
                var image =
                    "https://cdn-icons-png.flaticon.com/512/3177/3177440.png"
                if (massageModel.image == "") {
                    Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imUser)
                } else {
                    Log.d("ooo", "image ========== $image")

                    image = massageModel.image!!
                    Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imUser)
                }
            }
        }
    }

    class SentMessageViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val binding = ItemMessageSentBinding.bind(view)

        fun bind(massageModel: MassageModel){
            binding.tvMassage.text = massageModel.massage
            binding.tvTime.text = massageModel.time
            binding.apply {
                var image =
                    "https://cdn-icons-png.flaticon.com/512/3177/3177440.png"
                if (massageModel.image == "") {
                    Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imUser)
                } else {
                    Log.d("ooo", "image ========== $image")

                    image = massageModel.image!!
                    Glide.with(itemView.context)
                        .load(image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imUser)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.nameMassageUid == auth!!.uid) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: MutableList<MassageModel>) {
        messages = list
        notifyDataSetChanged()
    }
}
