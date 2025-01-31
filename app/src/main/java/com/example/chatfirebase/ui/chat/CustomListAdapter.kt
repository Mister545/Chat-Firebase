package com.example.chatfirebase.ui.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.R
import com.example.chatfirebase.ui.newGroup.NewGroupAdapter

class CustomListAdapter(
    private val context: Context,
    private val items: List<Item>,
    private val isEdit: Boolean,
    private val onItemClick: (Item) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_custom_item, parent, false)

        val item = items[position]
        val icon: ImageView = view.findViewById(R.id.iconUserList)
        val text: TextView = view.findViewById(R.id.textUserList)

        Glide.with(context)
            .load(item.iconUri)
            .error(R.drawable.user_default)
            .apply(RequestOptions.circleCropTransform())
            .into(icon)

        text.text = item.text

        val bDeleteUser = view.findViewById<ImageView>(R.id.deleteItem)

        if (isEdit){
            bDeleteUser.visibility = View.VISIBLE
        }else{
            bDeleteUser.visibility = View.GONE
        }
        bDeleteUser.setOnClickListener {
            onItemClick(item)
            Log.d("ooo", "clicked item list : ${item.userUid}")
        }

        return view
    }
}

data class Item(val iconUri: String, val text: String, val userUid: String, val chatId: String)
