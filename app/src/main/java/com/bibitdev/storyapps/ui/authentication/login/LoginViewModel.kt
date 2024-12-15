package com.bibitdev.storyapps.ui.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibitdev.storyapps.model.LoginResponse
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val resultLogin = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> get() = resultLogin

    private val load = MutableLiveData(false)
    val isFetching: LiveData<Boolean> get() = load

    private val errorStatus = MutableLiveData<String>()
    val error: LiveData<String> get() = errorStatus

    fun login(email: String, password: String) {
        load.value = true

        viewModelScope.launch {
            try {
                val response = userLogin(email, password)
                resultLogin.value = response
            } catch (exception: Exception) {
                errorStatus.value = exception.localizedMessage ?: "Terjadi kesalahan yang tidak terduga"
            } finally {
                load.value = false
            }
        }
    }

    private suspend fun userLogin(email: String, password: String): LoginResponse {
        return withContext(Dispatchers.IO) {
            repository.login(email, password)
        }
    }
}
