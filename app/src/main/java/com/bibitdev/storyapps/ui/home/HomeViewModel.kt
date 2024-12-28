package com.bibitdev.storyapps.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val repository: UserRepository) : ViewModel() {

    private val errorStatus = MutableLiveData<String>()
    val error: LiveData<String> = errorStatus

    fun getStories(token: String): Flow<PagingData<DataStory>> {
        return repository.getStoriesPaging(token).cachedIn(viewModelScope)
    }
}
