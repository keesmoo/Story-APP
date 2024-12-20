package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.model.Story
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(
    private val token: String,
    private val location: Int? = 1
) : PagingSource<Int, Story>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val page = params.key ?: 1  // Halaman pertama adalah 1
        return try {
            val response = ApiConfig.getApiService().getStories("Bearer $token", location, page, params.loadSize)
            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (stories.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        // Mengembalikan halaman pertama saat refresh
        return state.anchorPosition?.let { state.closestPageToPosition(it)?.prevKey?.plus(1) }
    }
}
