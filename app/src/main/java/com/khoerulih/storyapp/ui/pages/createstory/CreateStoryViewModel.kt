package com.khoerulih.storyapp.ui.pages.createstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khoerulih.storyapp.data.ApiConfig
import com.khoerulih.storyapp.data.CreateStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateStoryViewModel: ViewModel() {

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun createStory(token: String?, image: MultipartBody.Part, description: RequestBody) {
        val client = ApiConfig.getApiService().createStory("Bearer $token", image, description)
        client.enqueue(object : Callback<CreateStoryResponse> {
            override fun onResponse(call: Call<CreateStoryResponse>, response: Response<CreateStoryResponse>) {
                if (response.isSuccessful) {
                   _isError.value = false
                } else {
                    _isError.value = true
                    Log.e(TAG, "onFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CreateStoryResponse>, t: Throwable) {
                _isError.value = true
                Log.e(TAG, "onFailure : ${t.message}")
            }
        })
    }

    companion object {
        private const val TAG = "CreateStoryViewModel"
    }
}