package com.bibitdev.storyapps

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

suspend fun <T> Flow<T>.getOrAwaitValue(): T {
    return this.take(1).first()
}