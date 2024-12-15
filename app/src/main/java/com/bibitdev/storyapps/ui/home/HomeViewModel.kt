package com.bibitdev.storyapps.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibitdev.storyapps.model.StoriesResponse
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: UserRepository) : ViewModel() {

    private val storiesList = MutableLiveData<StoriesResponse>()
    val stories: LiveData<StoriesResponse> = storiesList

    private val errorStatus = MutableLiveData<String>()
    val error: LiveData<String> = errorStatus

    fun fetchStories(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStories(token)
                storiesList.value = response
            } catch (e: Exception) {
                errorStatus.value = e.localizedMessage ?: "An unknown error occurred"
            }
        }
    }
}