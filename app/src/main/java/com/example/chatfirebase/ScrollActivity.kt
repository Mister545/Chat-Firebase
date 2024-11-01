package com.example.chatfirebase

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatfirebase.RcView.ItemFragment
import com.example.chatfirebase.RcView.ModelUserRv
import com.example.chatfirebase.RcView.ViewAdapter
import com.example.chatfirebase.databinding.ActivityScrollBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ScrollActivity : AppCompatActivity(), ViewAdapter.OnItemClickListener {

    private lateinit var binding: ActivityScrollBinding

    private val adapter =  ViewAdapter(this)
    private val firebaseService = FirebaseService()
    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid
    private lateinit var authNull: FirebaseAuth
    private var btAddClose = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        authNull = FirebaseAuth.getInstance()
        if (FirebaseAuth.getInstance().currentUser == null){
            val intent = Intent(this, SignInAct::class.java)
            startActivity(intent)
            finish()
        }else{
            firebaseService.getUser(auth.uid!!) {
                binding.include.toolbarName.text = getString(R.string.toolbar_name, it.name)
            }

            binding.include.customToolbar.visibility = View.VISIBLE
            binding.toolbar.visibility = View.VISIBLE
            binding.fragmentContainer.visibility = View.GONE
            binding.animEmpty.visibility =View.GONE
            binding.animLoading.visibility = View.VISIBLE
            addNewChat()
            setIcAvatar()


            binding.fBAddNewChat.setOnClickListener {
                if (btAddClose){
//                    binding.fBAddNewChat.visibility = View.V
                    binding.rcView.visibility = View.GONE
                    binding.include.customToolbar.visibility = View.GONE
                    binding.toolbar.visibility = View.GONE
                    binding.animEmpty.visibility = View.GONE
                    binding.fBAddNewChat.setImageResource(R.drawable.ic_arrow_back_24)
                    val itemFragment = ItemFragment().apply {
                        arguments = Bundle().apply {
                            putBoolean("searchActive", true)
                        }
                    }
                    btAddClose = !btAddClose
                    replaceFragment(itemFragment)

                }else{
                    recreate()
                    binding.fBAddNewChat.setImageResource(R.drawable.baseline_add_24)
                    btAddClose = !btAddClose
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateAdapter()
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                this.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                uploadImageToFirebase(uri)
                Log.d("ooo", "Selected image URI ================= $uri")
            }
        }
    }

    private fun setIcAvatar() {
            firebaseService.getImage(uid!!) { imageUrl ->
                val storageRef = FirebaseStorage.getInstance().reference
                val imagesRef = storageRef.child(imageUrl.toUri().lastPathSegment.toString())

                val image =
                    R.drawable.ic_def_avatar
                Glide.with(this)
                    .load(image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.include.avatar)

                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(this)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.include.avatar)
                }.addOnSuccessListener {
                    binding.animLoading.visibility = View.GONE
                }.addOnFailureListener {
                    binding.animLoading.visibility = View.GONE
                }
//                    Toast.makeText(this, "Помилка завантаження фото: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        binding.animLoading.visibility = View.VISIBLE

        Log.d("ooo", "image URI : ${imageUri.lastPathSegment}")

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${imageUri.lastPathSegment}")
        val uploadTask = storageRef.putFile(imageUri)


        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                uid?.let { uidNonNull ->
                    setIcAvatar()
                    firebaseService.setImage(mapOf(uidNonNull to uri.toString()))
                    Log.d("ooo", "image URI Success: ${uri.toString()}")
                }
            }.addOnFailureListener { exception ->
                Log.d("ooo", "Error getting download URL: ${exception.message}")
            }
        }.addOnFailureListener {
            Log.d("ooo", "image URI Failure")
        }
    }

    fun clickOnImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        resultLauncher.launch(intent)
    }



        @SuppressLint("SuspiciousIndentation")
        override fun onItemClick(position: Int) {

        val clickedItem = adapter.getList()[position]
        val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("clickedItem", clickedItem)
        startActivity(intent)
            Log.d("ooo", "Clicked: ${clickedItem.codeChat}, ${clickedItem.names[1].name}")
        }


    private fun initRv() {
            binding.animEmpty.visibility = View.VISIBLE
        firebaseService.getAllUsers { listUsers ->
            val me = listUsers[uid]
            val listNew = arrayListOf<ModelUserRv>()
            firebaseService.getAllChats { isNull, listChats ->
                if (isNull) {
                    Log.d("ooo", "list Chats ============ nulll")
                } else {
                    if (me!!.chats != null || listChats[""]?.participants!![0] != "null") {
                        var chatsSize = me.chats!!.size

                        me.chats.forEach { myChat ->
                            val userId =
                                listChats[myChat]!!.participants.filter { it != uid }.joinToString()
                            Log.d("ooo", "array List null???? Chats ============ ${
                                arrayListOf(
                                    listUsers[userId]!!,
                                    me
                                )
                            }")
                            listNew.add(
                                ModelUserRv(
                                    myChat,
                                    arrayListOf(listUsers[userId]!!, me),
                                    listChats[myChat]!!.lastTime,
                                    listChats[myChat]!!.lastMassage
                                )
                            )
                            chatsSize--
                            Log.d("ooo", "chatsSize ========== $chatsSize")
                            if (listNew.size > 0)
                                binding.animEmpty.visibility = View.GONE

                            if (chatsSize == 0) {
                                adapter.setListMassages(listNew, me.name)
                                binding.rcView.layoutManager = LinearLayoutManager(this)
                                binding.rcView.adapter = adapter
                            }

                            Log.d("ooo", "listNew ========== ${listNew.toString()}")
                        }
                    } else
                        Log.d("ooo", "null ===============${me}")
                }
            }
        }
    }

    private fun updateAdapter(){
        firebaseService.getAllChats { isNull, list ->
            if (isNull){
                Log.d("ooo", "list Chats ============ nulll $list, $isNull")
            }else{
                initRv()
            }
        }
    }


    private fun addNewChat(){
        val data = intent.getStringExtra("data")

        if (data != null) {
            firebaseService.getAllUsers { listUsers ->
                val codeChat = (0..500000).random().toString()
                val user = listUsers[data]
                val me = listUsers[auth.uid]
                firebaseService.setChat(
                    ChatModel(
                        mutableListOf(data, auth.uid!!),
                        mutableListOf(MassageModel("","",""))
                    ),
                    codeChat
                )

                val updatedChatsMe = me?.chats?.apply {
                    add(codeChat)
                } ?: mutableListOf(codeChat)
                firebaseService.setUserState(
                    UserModel(
                        updatedChatsMe,
                        me!!.email,
                        me.name,
                        me.image),
                    auth.uid!!)
                val updatedChatsUser = user?.chats?.apply {
                    add(codeChat)
                } ?: mutableListOf(codeChat)
                firebaseService.setUserState(
                    UserModel(
                        updatedChatsUser,
                        user!!.email,
                        user.name,
                        user.image),
                    data)
            }
        }
        Log.d("ooo", "data ========== $data")
    }


    fun searchUser(text: String){

        if (text.isNotEmpty()) {
            firebaseService.getAllUsers { users ->

                val usersModelAr = users.values
                val usersAr: ArrayList<String> = arrayListOf()
                for (userName in usersModelAr){
                    usersAr.add(userName.name)
                }
                val filteredUsers: ArrayList<String> = usersAr.filter { it.contains(text, ignoreCase = true) } as ArrayList<String>

                val itemFragment = ItemFragment().apply {
                    arguments = Bundle().apply {
                        putStringArrayList("users", filteredUsers)
                    }
                }


                Log.d("ooo", filteredUsers.toString())
                replaceFragment(itemFragment)


            }
        }
    }

    private fun replaceFragment(fragment: Fragment){
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

    }
}
