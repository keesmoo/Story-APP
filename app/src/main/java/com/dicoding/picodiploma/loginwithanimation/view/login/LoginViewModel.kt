package com.dicoding.picodiploma.loginwithanimation.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository, private val apiService: ApiService) : ViewModel() {

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = apiService.login(credentials)

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val loginResult = loginResponse?.loginResult

                    if (loginResult != null && loginResponse.error == false) {
                        val userModel = UserModel(email, loginResult.token ?: "", isLogin = true)
                        repository.saveSession(userModel)
                        onResult(true, null)
                    } else {
                        onResult(false, loginResponse?.message ?: "Login failed.")
                    }
                } else {
                    onResult(false, "Login failed. Please check your credentials.")
                }
            } catch (e: Exception) {
                onResult(false, "An error occurred: ${e.message}")
            }
        }
    }
}
