package com.ys.callwidget

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


//data class CallWidgetState(
//    val name: String? = null,
//    val phone: String? = null,
//) {
//
//}

class CallWidgetViewModel(private val userDao: UserDao) : ViewModel() {


    val name :MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val phone :MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val personPicPath :MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val personPicUri :MutableLiveData<Uri> by lazy {
        MutableLiveData<Uri>()
    }
//    private val _callWidgetState = MutableStateFlow(CallWidgetState())
//    val callWidgetState = _callWidgetState.asStateFlow()

    fun updateValue(name:String,phone:String) {


    }

    fun saveUser(){
        var user = User(name = name.value.toString(), phone = phone.value.toString(), picture = personPicPath.value.toString())
        viewModelScope.launch {
            userDao.insert(user)
        }
    }


    fun listAll(): List<User> {
        var users = userDao.selectAll();
        return users
    }

    class CallWidgetViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CallWidgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CallWidgetViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // Define ViewModel factory in a companion object
//    companion object {
//
//        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : ViewModel> create(
//                modelClass: Class<T>,
//                extras: CreationExtras
//            ): T {
//                // Get the Application object from extras
//                val application = checkNotNull(extras[APPLICATION_KEY])
//                // Create a SavedStateHandle for this ViewModel from extras
//                val savedStateHandle = extras.createSavedStateHandle()
//
//                return CallWidgetViewModel() as T
//            }
//        }
//    }


}