package com.bibitdev.storyapps.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<DataStory>>()
    val stories: LiveData<List<DataStory>> get() = _stories

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getStoriesWithLocation("Bearer $token")
                _stories.value = response.listStory
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}