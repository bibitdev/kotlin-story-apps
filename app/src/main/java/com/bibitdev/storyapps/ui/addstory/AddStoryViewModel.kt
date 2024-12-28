package com.bibitdev.storyapps.ui.addstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bibitdev.storyapps.model.Response
import com.bibitdev.storyapps.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val repository: UserRepository) : ViewModel() {

    private val upResult = MutableLiveData<Response>()
    val uploadResult: LiveData<Response> get() = upResult

    fun uploadStory(
        token: String, file: MultipartBody.Part, description: RequestBody, lat: Double? = null,
        lon: Double? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("AddStoryViewModel", "Meng-upload story dengan token: $token")

                val response = repository.uploadStory(token, file, description)

                Log.d("AddStoryViewModel", "Response upload: $response")

                upResult.postValue(response)
            } catch (e: Exception) {
                Log.e("AddStoryViewModel", "Error saat upload: ${e.localizedMessage}")

                upResult.postValue(
                    Response(
                        error = true,
                        message = e.localizedMessage ?: "Terjadi kesalahan yang tidak diketahui"
                    )
                )
            }
        }
    }
}
