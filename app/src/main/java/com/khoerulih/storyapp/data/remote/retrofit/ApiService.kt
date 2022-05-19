package com.khoerulih.storyapp.data.remote.retrofit

import com.khoerulih.storyapp.data.remote.responses.CreateStoryResponse
import com.khoerulih.storyapp.data.remote.responses.LoginResponse
import com.khoerulih.storyapp.data.remote.responses.RegisterResponse
import com.khoerulih.storyapp.data.remote.responses.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun userRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun userLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ) : Call<LoginResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    @GET("stories?location=1")
    fun getAllStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int = 200
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun createStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?,
    ): Call<CreateStoryResponse>
}