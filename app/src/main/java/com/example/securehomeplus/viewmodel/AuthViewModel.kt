package com.example.securehomeplus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securehomeplus.data.database.entities.User
import com.example.securehomeplus.data.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class AuthViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<User?>()
    val loginResult: LiveData<User?> get() = _loginResult

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean> get() = _registerResult

    fun registerUser(name: String, email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val alreadyExists = repository.isUserRegistered(email)
            if (!alreadyExists) {
                repository.registerUser(User(name = name, email = email, password = password))
                _registerResult.postValue(true)
            } else {
                _registerResult.postValue(false)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.loginUser(email, password)
            withContext(Dispatchers.Main) {
                _loginResult.value = user
            }
        }
    }
}
