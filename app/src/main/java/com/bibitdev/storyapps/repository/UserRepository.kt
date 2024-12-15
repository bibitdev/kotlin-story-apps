package com.bibitdev.storyapps.repository

import android.util.Log
import com.bibitdev.storyapps.api.ApiService
import com.bibitdev.storyapps.model.LoginResponse
import com.bibitdev.storyapps.model.Response
import com.bibitdev.storyapps.model.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val apiService: ApiService) {

    suspend fun register(name: String, email: String, password: String): Response {
        return safeApiCall {
            apiService.register(name, email, password)
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return safeApiCall {
            apiService.login(email, password)
        }
    }


    suspend fun getStories(token: String): StoriesResponse {
        return safeApiCall {
            apiService.getStories("Bearer $token")
        }
    }

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Response {
        return safeApiCall {
            apiService.uploadStory("Bearer $token", file, description)
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: Exception) {
            Log.e("Register", "API call failed: ${e.message}")
            throw Exception("API call failed: ${e.message}")
        }
    }
}
