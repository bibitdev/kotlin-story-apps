package com.bibitdev.storyapps.model

data class DataUser(
    val userId: String,
    val name: String,
    val token: String
)

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: DataUser
)
