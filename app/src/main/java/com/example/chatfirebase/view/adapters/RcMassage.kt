package com.example.chatfirebase.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.DataTimeHelper
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.R
import com.example.chatfirebase.data.User
import com.example.chatfirebase.data.Message
import com.example.chatfirebase.databinding.ItemMessageReceivedBinding
import com.example.chatfirebase.databinding.ItemMessageSentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RcMassage(
    private val onEdit: (Pair<String, Message>) -> Unit,
    private val onDelete: (Pair<String, Message>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messages = arrayListOf<Pair<String, Message>>()
    val auth = Firebase.auth.currentUser

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    inner class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMessageReceivedBinding.bind(view)

        @SuppressLint("NewApi")
        fun bind(message: Pair<String, Message>) {
            binding.tvMassage.text = message.second.message
            binding.tvTime.text = DataTimeHelper().convertIsoUtcFToLocal(message.second.time!!)

            binding.apply {
                getAllData(message.second) { _, user ->
                    Glide.with(itemView.context)
                        .load(user.image)
                        .error(R.drawable.user_default)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imUser)
                }
            }

            itemView.setOnLongClickListener {
                showPopupMenu(it, message, isSent = false)
                true
            }
        }
        private fun getAllData(massageModel: Message, callback: (Message, User) -> Unit) {
            FirebaseRepositoryImpl().getUser(massageModel.nameMassageUid!!) { user ->
                callback(massageModel, user)
            }
        }

    }

    inner class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMessageSentBinding.bind(view)

        @SuppressLint("NewApi")
        fun bind(message: Pair<String, Message>) {
            binding.tvMassage.text = message.second.message
            binding.tvTime.text = Instant.parse(message.second.time)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH:mm"))

            itemView.setOnLongClickListener {
                showPopupMenu(it, message, isSent = true)
                true
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.second.nameMassageUid == auth!!.uid) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(inflater.inflate(R.layout.item_message_sent, parent, false))
        } else {
            ReceivedMessageViewHolder(inflater.inflate(R.layout.item_message_received, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) holder.bind(message)
        else if (holder is ReceivedMessageViewHolder) holder.bind(message)
    }

    override fun getItemCount() = messages.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<Pair<String, Message>>) {
        messages = list
        notifyDataSetChanged()
    }

    private fun showPopupMenu(view: View, message: Pair<String, Message>, isSent: Boolean) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.message_menu, popup.menu)

        if (!isSent) {
            popup.menu.findItem(R.id.editMessage).isVisible = false
        }

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.editMessage -> onEdit(message)
                R.id.deleteMessage -> onDelete(message)
            }
            true
        }
        popup.show()
    }
}

