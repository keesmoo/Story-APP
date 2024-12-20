package com.dicoding.picodiploma.loginwithanimation.view.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dicoding.picodiploma.loginwithanimation.DataDummy
import com.dicoding.picodiploma.loginwithanimation.MainDispatcherRule
import com.dicoding.picodiploma.loginwithanimation.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when fetching stories is successful, stories should not be null and data is correct`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()

        val storyViewModel = StoryViewModel()
        storyViewModel.setStoriesForTesting(dummyStories)

        val result = storyViewModel.stories.getOrAwaitValue()

        assertNotNull(result)
        assertEquals(dummyStories.size, result.size)
        assertEquals(dummyStories[0], result[0])
    }

    @Test
    fun `when no stories are returned, stories should be empty`() = runTest {
        val storyViewModel = StoryViewModel()
        storyViewModel.setStoriesForTesting(emptyList())

        val result = storyViewModel.stories.getOrAwaitValue()

        assertNotNull(result)
        assertEquals(0, result.size)
    }
}
