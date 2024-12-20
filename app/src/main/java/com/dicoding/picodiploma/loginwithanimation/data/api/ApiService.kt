package com.dicoding.picodiploma.loginwithanimation.data.api

import com.dicoding.picodiploma.loginwithanimation.data.model.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.DetailResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.RegisterResponse
import com.dicoding.picodiploma.loginwithanimation.data.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body credentials: Map<String, String>): Response<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("location") location: Int? = 1,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): Response<StoryResponse>

    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Response<DetailResponse>

    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part? = null,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<AddStoryResponse>

}