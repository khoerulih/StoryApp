package com.khoerulih.storyapp.ui.pages.main

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.khoerulih.storyapp.data.StoryRepository
import com.khoerulih.storyapp.data.remote.retrofit.ApiConfig
import com.khoerulih.storyapp.data.remote.responses.ListStoryItem
import com.khoerulih.storyapp.data.remote.responses.StoryResponse
import com.khoerulih.storyapp.utils.SettingPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(storyRepository: StoryRepository, private val pref: SettingPreferences) : ViewModel() {
    private var token: String

    init{
        runBlocking{
            token = pref.getUserSession().first()
        }
    }

    val listStory: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)

    companion object {
        private const val TAG = "MainViewModel"
    }
}