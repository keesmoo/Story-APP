package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiConfig
import com.dicoding.picodiploma.loginwithanimation.data.api.ApiService

object Injection {
    fun provideRepository(context: Context): UserRepository {
        return UserRepository.getInstance(context)
    }

    fun provideApiService(): ApiService {
        return ApiConfig.getApiService()
    }
}
