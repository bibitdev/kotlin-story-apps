package com.bibitdev.storyapps

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = mutableListOf<T>()
        val observer = Observer<T> { data.add(it) }
        liveData.observeForever(observer)
        return data[0]
    }
}