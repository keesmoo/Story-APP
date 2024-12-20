package com.dicoding.picodiploma.loginwithanimation.view.story

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.model.Story
import kotlinx.coroutines.launch

class StoryViewModel : ViewModel() {


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchStories(token: String, withLocation: Boolean = false) = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { StoryPagingSource(token, if (withLocation) 1 else null) }
    ).flow

    // The old approach, using a list, is still available for fallback
    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

//    fun fetchStoriesOld(token: String, withLocation: Boolean = false) {
//        viewModelScope.launch {
//            try {
//                val locationParam = if (withLocation) 1 else null
//                val response = ApiConfig.getApiService().getStories("Bearer $token", locationParam ?: 1)
//                if (response.isSuccessful) {
//                    val storiesList = response.body()?.listStory
//                    _stories.postValue(storiesList)
//                    storiesList?.forEach { story ->
//                        Log.d("StoryViewModel", "Story ID: ${story.id}, Lat: ${story.lat}, Lon: ${story.lon}")
//                    }
//                } else {
//                    _error.postValue("Failed to fetch stories")
//                }
//            } catch (e: Exception) {
//                _error.postValue("Error: ${e.message}")
//            }
//        }
//    }

    @VisibleForTesting
    fun setStoriesForTesting(stories: List<Story>) {
        _stories.postValue(stories)
    }

}
