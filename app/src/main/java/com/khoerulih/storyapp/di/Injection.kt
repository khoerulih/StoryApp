package com.khoerulih.storyapp.di

import android.content.Context
import com.khoerulih.storyapp.data.StoryRepository
import com.khoerulih.storyapp.data.local.room.StoryDatabase
import com.khoerulih.storyapp.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}