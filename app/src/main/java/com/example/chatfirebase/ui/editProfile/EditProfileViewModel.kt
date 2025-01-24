package com.example.chatfirebase.ui.editProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatfirebase.FirebaseService
import com.example.chatfirebase.UserModel
import com.google.firebase.auth.FirebaseAuth

class EditProfileViewModel : ViewModel() {

    var dataOfBirth: MutableLiveData<String> = MutableLiveData()
    val user: MutableLiveData<UserModel> = MutableLiveData()
    private val firebaseService = FirebaseService()
    private val uid = FirebaseAuth.getInstance().uid

    init {
        getDataUser()
    }

    private fun getDataUser() {
        firebaseService.getUserSingleTime(uid!!) {
            user.value = it
        }
    }

    fun changeUserData(name: String, secondName: String, aboutYou: String, callback: (Boolean) -> Unit) {
        val newUserData: UserModel = user.value!!
        newUserData.name = name
        newUserData.introduceYourSelf = aboutYou
        newUserData.dateOfBerth = if(dataOfBirth.value.isNullOrEmpty()) user.value!!.dateOfBerth else dataOfBirth.value

        firebaseService.setUserState(newUserData, uid!!){
            callback(it)
        }
    }
}