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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RcMassage : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var messages = listOf<MessageModel>()
    val firebaseService = FirebaseService()

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2
    val auth = Firebase.auth.currentUser

    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val firebaseService = FirebaseService()
        val binding = ItemMessageReceivedBinding.bind(view)

        @SuppressLint("NewApi")
        fun bind(massageModel: MessageModel) {
            binding.tvMassage.text = massageModel.massage
            binding.tvTime.text = DataTimeHelper().convertIsoUtcFToLocal(massageModel.time!!)
            binding.apply {
                var image =
                    "https://cdn-icons-png.flaticon.com/512/3177/3177440.png"

                getAllData(massageModel) { massageModel, user ->
                    if (user.image == "") {
                        Glide.with(itemView.context)
                            .load(R.drawable.im_dog)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imUser)
                    } else {
                        Log.d("ooo", "image ========== $image")

                        image = user.image
                        Glide.with(itemView.context)
                            .load(image)
                            .apply(RequestOptions.circleCropTransform())
                            .into(binding.imUser)
                    }
                }
            }
        }

        fun getAllData(massageModel: MessageModel, callback: (MessageModel, UserModel) -> Unit) {
            firebaseService.getUser(massageModel.nameMassageUid!!) { user ->
                callback(massageModel, user)
            }
        }
    }


    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val firebaseService = FirebaseService()
        val binding = ItemMessageSentBinding.bind(view)

        @SuppressLint("NewApi")
        fun bind(massageModel: MessageModel) {
            binding.tvMassage.text = massageModel.massage
            binding.tvMassage.text = massageModel.massage
            val utcTime = Instant.parse(massageModel.time) // або завантажений з бази
            val localTime = utcTime.atZone(ZoneId.systemDefault()) // Конвертуємо в локальний час
            val formattedTime = localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            binding.tvTime.text = formattedTime
            binding.apply {
                val imageDefault =
                    "https://cdn-icons-png.flaticon.com/512/3177/3177440.png"

                getAllData(massageModel) { massageModel, user ->
                    Glide.with(itemView.context)
                        .load(user.image)
                        .error(imageDefault)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imUser)
                }
            }
        }

        fun getAllData(massageModel: MessageModel, callback: (MessageModel, UserModel) -> Unit) {
            firebaseService.getUser(massageModel.nameMassageUid!!) { user ->
                callback(massageModel, user)
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
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
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
    fun setList(list: List<MessageModel>) {
        Log.d("ooo", "listtttt $list")
        messages = list
        notifyDataSetChanged()
    }
}
