package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository.getInstance(applicationContext)

        setupView()
        setupAction()
    }

    private fun setupView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.hide(android.view.WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (name.isEmpty()) {
                binding.nameEditText.error = getString(R.string.name_required)
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.passwordEditText.error = getString(R.string.password_too_short)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                registerUser(email, password, name)
            }
        }
    }


    private suspend fun registerUser(email: String, password: String, name: String) {
        val credentials = mapOf(
            "name" to name,
            "email" to email,
            "password" to password
        )

        try {
            val response = ApiConfig.getApiService().register(credentials)

            if (response.isSuccessful) {
                val registerResponse = response.body()
                if (registerResponse?.error == false) {
                    Toast.makeText(applicationContext, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()

                    val userModel = UserModel(email, "", isLogin = true)
                    userRepository.saveSession(userModel)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Gagal registrasi: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                    println("Registration failed: ${registerResponse?.message}")
                }
            } else {
                println("Error: ${response.code()} - ${response.message()}")
                Toast.makeText(applicationContext, "Terjadi kesalahan saat registrasi", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()  // Log the exception for debugging
            Toast.makeText(applicationContext, "Terjadi kesalahan saat registrasi", Toast.LENGTH_SHORT).show()
        }
    }

}
