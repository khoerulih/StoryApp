package com.khoerulih.storyapp.ui.pages.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khoerulih.storyapp.data.remote.retrofit.ApiConfig
import com.khoerulih.storyapp.data.remote.responses.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    fun register(name: String, email: String, password: String){
        val client = ApiConfig.getApiService().userRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    _isError.value = false
                } else {
                    _isError.value = true
                    Log.e(TAG, "registerFailure : ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isError.value = false
                Log.e(TAG, "onFailure : ${t.message}")
            }

        })
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}