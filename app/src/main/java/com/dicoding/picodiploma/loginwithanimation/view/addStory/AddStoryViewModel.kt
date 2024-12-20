package com.dicoding.picodiploma.loginwithanimation.view.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.model.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel : ViewModel() {

    private val _uploadResult = MutableLiveData<AddStoryResponse>()
    val uploadResult: LiveData<AddStoryResponse> = _uploadResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun uploadStory(
        token: String,
        description: RequestBody,
        file: MultipartBody.Part?,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().addStory(
                    token = "Bearer $token",
                    file = file,
                    description = description,
                    lat = lat,
                    lon = lon

                )
                if (response.isSuccessful) {
                    response.body()?.let {
                        _uploadResult.postValue(it)
                    } ?: run {
                        _error.postValue("Response body is null")
                    }
                } else {
                    _error.postValue("Failed to upload story: ${response.message()}")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
