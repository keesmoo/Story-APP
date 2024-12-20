package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.model.Story
import com.dicoding.picodiploma.loginwithanimation.view.addStory.AddStoryFragment
import com.dicoding.picodiploma.loginwithanimation.view.details.DetailsFragment
import com.dicoding.picodiploma.loginwithanimation.view.main.MapsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoryFragment : Fragment() {

    private val viewModel: StoryViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.fragment_story, container, false)

        val recyclerView: RecyclerView = binding.findViewById(R.id.rv_story_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        storyAdapter = StoryAdapter { storyId ->
            val bundle = Bundle().apply { putString("STORY_ID", storyId) }
            val detailsFragment = DetailsFragment().apply { arguments = bundle }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = storyAdapter

        // Observe stories live data from ViewModel
        lifecycleScope.launch {
            val userRepository = UserRepository.getInstance(requireContext())
            userRepository.getSession().collect { userModel ->
                val token = userModel.token
                if (token.isNotEmpty()) {
                    // Collect the PagingData and pass it to the adapter
                    viewModel.fetchStories(token, withLocation = true).collectLatest { pagingData: PagingData<Story> ->
                        storyAdapter.submitData(pagingData)
                    }
                    Log.d("StoryFragment", "Token successfully retrieved")
                } else {
                    Log.d("StoryFragment", "User is not logged in.")
                }
            }
        }

        // FAB for adding story
        val fabAddStory: FloatingActionButton = binding.findViewById(R.id.fabAddStory)
        fabAddStory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddStoryFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        // FAB for Maps
        val fabMaps: FloatingActionButton = binding.findViewById(R.id.fabMaps)
        fabMaps.setOnClickListener {
            // Mengambil data stories dari PagingData yang telah di-submit ke adapter
            val stories = storyAdapter.snapshot().items
            if (stories.isNotEmpty()) {
                val intent = Intent(requireContext(), MapsActivity::class.java)
                intent.putParcelableArrayListExtra("STORY_LIST", ArrayList(stories))
                startActivity(intent)
            } else {
                Log.d("StoryFragment", "No stories to show on map.")
            }
        }

        return binding
    }
}
