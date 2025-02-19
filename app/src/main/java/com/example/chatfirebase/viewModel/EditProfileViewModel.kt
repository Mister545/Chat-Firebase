package com.example.chatfirebase.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.model.repository.FirebaseRepositoryImpl
import com.example.chatfirebase.data.User
import com.google.firebase.auth.FirebaseAuth

class EditProfileViewModel : ViewModel() {

    var dataOfBirth: MutableLiveData<String> = MutableLiveData()
    val user: MutableLiveData<User> = MutableLiveData()
    private val firebaseRepositoryImpl = FirebaseRepositoryImpl()
    private val uid = FirebaseAuth.getInstance().uid

    init {
        getDataUser()
    }

    private fun getDataUser() {
        firebaseRepositoryImpl.getUserSingleTime(uid!!) {
            user.value = it
        }
    }

    fun changeUserData(name: String, secondName: String, aboutYou: String, callback: (Boolean) -> Unit) {
        val newUserData: User = user.value!!
        newUserData.name = name
        newUserData.introduceYourSelf = aboutYou
        newUserData.dateOfBerth = if(dataOfBirth.value.isNullOrEmpty()) user.value!!.dateOfBerth else dataOfBirth.value

        firebaseRepositoryImpl.setUserState(newUserData, uid!!){
            callback(it)
        }
    }
}