package com.dicoding.picodiploma.loginwithanimation.view.details

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userRepository = UserRepository.getInstance(requireContext())
        lifecycleScope.launch {
            userRepository.getSession().collect { userModel ->
                val token = userModel.token
                if (token.isNotEmpty()) {
                    arguments?.getString("STORY_ID")?.let { storyId ->
                        viewModel.fetchStoryDetail(storyId, token)
                    }
                    Log.d("StoryFragment", "token berhasil diambil")
                } else {
                    Log.d("StoryFragment", "User is not logged in.")
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.storyDetail.observe(viewLifecycleOwner) { story ->
            if (story != null) {
                view.findViewById<TextView>(R.id.userName).text = story.name
                view.findViewById<TextView>(R.id.eventDescription).text = story.description

                Log.d("DetailsFragment", "Username berhasil diambil: ${story.name}")
                Log.d("DetailsFragment", "Deskripsi berhasil diambil: ${story.description}")

                Glide.with(requireContext())
                    .load(story.photoUrl)
                    .into(view.findViewById(R.id.storyPhoto))

                Log.d("DetailsFragment", "Photo URL: ${story.photoUrl}")
            } else {
                Log.d("DetailsFragment", "Story detail is null")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

}