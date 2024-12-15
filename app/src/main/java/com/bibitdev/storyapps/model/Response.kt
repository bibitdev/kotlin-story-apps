package com.bibitdev.storyapps.model


data class Response(
    val error: Boolean,
    val message: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: DataUser
)

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<DataStory>
)