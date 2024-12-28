package com.bibitdev.storyapps.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bibitdev.storyapps.repository.UserRepository
import com.bibitdev.storyapps.ui.addstory.AddStoryViewModel
import com.bibitdev.storyapps.ui.authentication.login.LoginViewModel
import com.bibitdev.storyapps.ui.authentication.register.RegisterViewModel
import com.bibitdev.storyapps.ui.home.HomeViewModel
import com.bibitdev.storyapps.ui.map.MapsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {

    private val creators: Map<Class<out ViewModel>, () -> ViewModel> = mapOf(
        LoginViewModel::class.java to { LoginViewModel(repository) },
        RegisterViewModel::class.java to { RegisterViewModel(repository) },
        HomeViewModel::class.java to { HomeViewModel(repository) },
        AddStoryViewModel::class.java to { AddStoryViewModel(repository) },
        MapsViewModel::class.java to {MapsViewModel(repository)}
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass]
            ?: throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        return creator() as T
    }
}