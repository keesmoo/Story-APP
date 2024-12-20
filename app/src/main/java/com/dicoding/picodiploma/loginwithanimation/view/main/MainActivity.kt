package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addStory.AddStoryFragment
import com.dicoding.picodiploma.loginwithanimation.view.details.DetailsFragment
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryFragment
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            Log.d("MainActivity", "User login status: ${user.isLogin}")
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                Log.d("MainActivity", "Navigating to StoryFragment")


                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, StoryFragment())
                    .commit()
            }
        }

        setupView()
        setupAction()
        // Tambahkan listener untuk perubahan fragment
        supportFragmentManager.addOnBackStackChangedListener {
            handleFabVisibility()
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.logoutFab.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun handleFabVisibility() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        binding.logoutFab.visibility = when (currentFragment) {
            is AddStoryFragment, is DetailsFragment -> View.GONE // Sembunyikan di halaman AddStory dan Detail
            else -> View.VISIBLE // Tampilkan di halaman lain
        }
    }


    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is StoryFragment) {
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}

