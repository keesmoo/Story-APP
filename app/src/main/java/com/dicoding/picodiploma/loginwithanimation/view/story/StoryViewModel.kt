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

    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> = _stories

    @VisibleForTesting
    fun setStoriesForTesting(stories: List<Story>) {
        _stories.postValue(stories)
    }

}
