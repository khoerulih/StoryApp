package com.khoerulih.storyapp.ui.pages.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khoerulih.storyapp.data.remote.responses.ListStoryItem
import com.khoerulih.storyapp.data.remote.responses.StoryResponse
import com.khoerulih.storyapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel: ViewModel() {
    private val _listStoryWithLocation = MutableLiveData<List<ListStoryItem>>()
    val listStoryWithLocation: LiveData<List<ListStoryItem>> = _listStoryWithLocation

    fun getAllStoryWithLocation(token: String) {
        val client = ApiConfig.getApiService().getAllStoriesWithLocation("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    _listStoryWithLocation.value = response.body()?.listStory as List<ListStoryItem>
                } else {
                    Log.e(TAG, "onFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure : ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "MapsViewModel"
    }
}