package com.khoerulih.storyapp.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences): ViewModel() {
    fun getSession(): LiveData<String> {
        return pref.getUserSession().asLiveData()
    }

    fun saveSession(token: String) {
        viewModelScope.launch {
            pref.saveUserSession(token)
        }
    }

    fun deleteSession(){
        viewModelScope.launch {
            pref.saveUserSession("")
        }
    }
}