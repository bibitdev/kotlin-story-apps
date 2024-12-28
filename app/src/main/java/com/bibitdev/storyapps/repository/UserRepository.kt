package com.bibitdev.storyapps.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bibitdev.storyapps.api.ApiService
import com.bibitdev.storyapps.model.DataStory
import com.bibitdev.storyapps.model.LoginResponse
import com.bibitdev.storyapps.model.Response
import com.bibitdev.storyapps.model.StoriesResponse
import com.bibitdev.storyapps.paging.PagingStory
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

open class UserRepository(private val apiService: ApiService) {

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

    suspend fun uploadStory(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody
    ): Response {
        return safeApiCall {
            apiService.uploadStory("Bearer $token", file, description)
        }
    }

    fun getStoriesPaging(token: String): Flow<PagingData<DataStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 3,
                initialLoadSize = 3,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { PagingStory(apiService, token) }
        ).flow
    }

    suspend fun getStoriesWithLocation(token: String): StoriesResponse {
        return safeApiCall {
            apiService.getStoriesWithLocation(
                token = token,
                location = 1,
            )
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): T {
        return try {
            apiCall()
        } catch (e: Exception) {
            throw Exception("API call failed: ${e.message}")
        }
    }
}
