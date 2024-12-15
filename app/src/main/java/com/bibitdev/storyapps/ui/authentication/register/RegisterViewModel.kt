package com.bibitdev.storyapps.ui.authentication.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibitdev.storyapps.model.Response
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterViewModel (private val repository: UserRepository) : ViewModel() {

    private val resultRegist = MutableLiveData<Response>()
    val registerResult: LiveData<Response> get() = resultRegist

    private val errorStatus = MutableLiveData<String>()
    val error: LiveData<String> get() = errorStatus

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = userRegister(name, email, password)
                resultRegist.value = response
            } catch (exception: Exception) {
                errorStatus.value = exception.localizedMessage ?: "An unexpected error occurred"
            }
        }
    }

    private suspend fun userRegister(name: String, email: String, password: String): Response {
        return withContext(Dispatchers.IO) {
            repository.register(name, email, password)
        }
    }
}
