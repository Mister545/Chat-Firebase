package com.example.chatfirebase.ui.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.ChatModel
import com.example.chatfirebase.DialogHelper
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.R
import com.example.chatfirebase.TYPE_CHAT
import com.example.chatfirebase.TYPE_GROUP
import com.example.chatfirebase.TYPE_TO_DO
import com.example.chatfirebase.UserModel
import com.example.chatfirebase.ui.ChatState

class ChatActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private var selectedChatId: String? = null

    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory(firebaseService)
        ).get(ChatViewModel::class.java)
    }
    val firebaseService = FirebaseService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val chatId = intent.getStringExtra("clickedItem")
        val clickedUser = intent.getStringExtra("clickedUser")
        val isToDo = intent.getStringExtra(TYPE_TO_DO)

        viewModel.start(chatId, clickedUser)

        viewModel.state.observe(this) {
            initDrawer(chatId, it)
            toolbarInitForChat()
            initToolbar(it)
        }
        setDataInFragment(chatId, clickedUser, isToDo)

    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    this.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.changeImageOfGrope(selectedChatId!!, uri)
                    Log.d("ooo", "Selected image URI: $uri, $selectedChatId")
                }
            }
        }

    private fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.END)
    }

    private fun initDrawer(chatId: String?, state: ChatState) {
        selectedChatId = chatId
        if (chatId != null) {
            makeDrawer()
            setDataInDrawer(state)
            changeNameOfGrope()
            changeImageOfGrope()
        }
    }

    private fun makeDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        val bOpenDrawer = findViewById<Button>(R.id.openDrawer)
        bOpenDrawer.setCompoundDrawablesWithIntrinsicBounds(
            DrawerArrowDrawable(this), null, null, null
        )
        bOpenDrawer.setOnClickListener {
            openDrawer()
        }
    }

    private fun setDataInFragment(chatId: String?, clickedUser: String?, isToDo: String?) {
        val fragment = ChatFragment()
        val bundle = Bundle()

        bundle.putString("clickedItem", chatId)
        bundle.putString("clickedUser", clickedUser)
        bundle.putString(TYPE_TO_DO, isToDo)
        Log.d("ooo", "is to do activity $isToDo")

        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
        Log.d("ooo", "intent to chat $chatId")
    }

    @SuppressLint("SetTextI18n")
    private fun setDataInDrawer(state: ChatState) {

        Log.d("ooo", "partners: $state")

        val usersCount = findViewById<TextView>(R.id.tUsersCount)
        val chatImage = findViewById<ImageView>(R.id.chatImage)
        val chatName = findViewById<TextView>(R.id.chatName)
        chatName.text = if (state.chatType?.nameOfGroup.isNullOrEmpty()) state.partner?.name
        else state.chatType?.nameOfGroup
        usersCount.text = state.partners.size.toString()
        val listItems = arrayListOf<Item>()
        state.partners.forEach { data ->
            listItems.add(Item(data.value.image, data.value.name, data.key, selectedChatId!!))
        }
        val image = if (state.chatType?.imageOfGroup.isNullOrEmpty()) state.partner?.image
        else state.chatType?.imageOfGroup
        Glide.with(this)
            .load(image)
            .error(R.drawable.user_default)
            .apply(RequestOptions.circleCropTransform())
            .into(chatImage)
        initListUsers(listItems, selectedChatId!!, false)
        updateUi(listItems, selectedChatId!!)
    }

    private fun toolbarInitForChat() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.p_ed_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            finish()
        }
    }

    private fun initListUsers(items: ArrayList<Item>, chatId: String, isEdit: Boolean) {
        val listView: ListView = findViewById(R.id.listHeader)
        val adapter = CustomListAdapter(this, items, isEdit) {
            deleteUserFromGrope(it, items, chatId)
        }

        listView.adapter = adapter
    }

    private fun changeNameOfGrope() {
        findViewById<ImageView>(R.id.editNameHeader).setOnClickListener {
            DialogHelper().showInputDialog(this) {
                viewModel.changeNameOfGrope(selectedChatId!!, it)
            }
        }
    }

    private fun changeImageOfGrope() {
        findViewById<ImageView>(R.id.editImageHeader).setOnClickListener {
                selectGroupImage()
        }
    }

    private fun deleteUserFromGrope(clickedItem: Item, items: ArrayList<Item>, chatId: String) {
        viewModel.deleteUserFromGrope(clickedItem, items, chatId)
    }

    private fun selectGroupImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultLauncher.launch(intent)
    }


    private fun initToolbar(state: ChatState) {
        when (state.chatType?.typeOfChat) {
            TYPE_TO_DO -> {
                initToDoToolbar()
            }

            TYPE_GROUP -> {
                initGropeToolbar(state.chatType)
            }

            TYPE_CHAT -> {
                initChatToolbar(state.partner!!)
            }

            else -> {
                initChatToolbar(state.partner)
            }
        }
    }

    private fun initChatToolbar(partner: UserModel?) {

        if (partner != null) {
            Log.d("ooo", "pertner $partner")
            val toolbarImage = findViewById<ImageView>(R.id.iconUser)
            val toolbarName = findViewById<TextView>(R.id.toolbarTitle)

            toolbarName.text = partner.name
            Glide.with(this)
                .load(partner.image)
                .error(R.drawable.user_default)
                .apply(RequestOptions.circleCropTransform())
                .into(toolbarImage)
        }
    }

    private fun initToDoToolbar() {
        val toolbarImage = findViewById<ImageView>(R.id.iconUser)
        val toolbarName = findViewById<TextView>(R.id.toolbarTitle)
        val isOnline = findViewById<TextView>(R.id.toolbarOnline)
        isOnline.text = " "
        toolbarName.text = "Saved"
        Glide.with(this)
            .load(R.drawable.bookmark_24px)
            .apply(RequestOptions.circleCropTransform())
            .into(toolbarImage)
    }

    private fun initGropeToolbar(chat: ChatModel) {
        Log.d("ooo", "pertner $chat")
        val toolbarImage = findViewById<ImageView>(R.id.iconUser)
        val toolbarName = findViewById<TextView>(R.id.toolbarTitle)
        toolbarName.text = chat.nameOfGroup
        Glide.with(this)
            .load(chat.imageOfGroup)
            .error(R.drawable.group_ic)
            .apply(RequestOptions.circleCropTransform())
            .into(toolbarImage)
    }

    private fun updateUi(items: ArrayList<Item>, chatId: String){
        var isEdit = false
        val bSettings = findViewById<LinearLayout>(R.id.settingsHeader)
        val bChangeP = findViewById<ImageView>(R.id.editImageHeader)
        val bChangeN = findViewById<ImageView>(R.id.editNameHeader)
        bSettings.setOnClickListener {
            if (isEdit) {
                bChangeP.visibility = View.VISIBLE
                bChangeN.visibility = View.VISIBLE
            } else {
                bChangeP.visibility = View.GONE
                bChangeN.visibility = View.GONE
            }
            initListUsers(items, chatId, isEdit)
            isEdit = !isEdit
        }
    }
}