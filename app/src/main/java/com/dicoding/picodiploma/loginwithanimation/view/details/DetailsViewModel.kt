package com.dicoding.picodiploma.loginwithanimation.view.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.model.DetailStory
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel() {

    private val _storyDetail = MutableLiveData<DetailStory?>()
    val storyDetail: MutableLiveData<DetailStory?> = _storyDetail

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchStoryDetail(storyId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = ApiConfig.getApiService().getStoryDetail("Bearer $token", storyId)
                if (response.isSuccessful) {
                    val story = response.body()?.story
                    if (story != null) {
                        _storyDetail.postValue(story)
                    } else {
                        _error.postValue("Story detail is null")
                    }
                } else {
                    _error.postValue("Failed to fetch story detail")
                }
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
