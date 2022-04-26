package com.khoerulih.storyapp.ui.pages.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.khoerulih.storyapp.R
import com.khoerulih.storyapp.data.ListStoryItem
import com.khoerulih.storyapp.databinding.ActivityMainBinding
import com.khoerulih.storyapp.ui.pages.ListStoryAdapter
import com.khoerulih.storyapp.ui.pages.ViewModelFactory
import com.khoerulih.storyapp.ui.pages.createstory.CreateStoryActivity
import com.khoerulih.storyapp.ui.pages.login.LoginActivity
import com.khoerulih.storyapp.utils.SettingPreferences
import com.khoerulih.storyapp.utils.SettingViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]

        binding?.rvStory?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        binding?.rvStory?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding?.rvStory?.addItemDecoration(itemDecoration)

        settingViewModel.getSession().observe(this) { token ->
            if (token.isNullOrEmpty()) {
                goToLoginActivity()
            } else {
                mainViewModel.getAllStory(token)
            }
        }

        mainViewModel.listStory.observe(this) { story ->
            setListData(story)

            if(story.isNullOrEmpty()){
                Toast.makeText(this, getString(R.string.data_empty), Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding?.fabAdd?.setOnClickListener {
            goToCreateStoryActivity()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val pref = SettingPreferences.getInstance(dataStore)
                val settingViewModel =
                    ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_confirmation))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        val intent = LoginActivity.loginActivityIntent(this)
                        startActivity(intent)
                        settingViewModel.deleteSession()
                        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .setNegativeButton(getString(R.string.no)) { _, _ ->
                        // Do Nothing
                    }
                    .show()
                return true
            }
            R.id.localization -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            else -> return true
        }
    }

    private fun setListData(listUsers: List<ListStoryItem>) {
        val stories = ArrayList<ListStoryItem>()
        for (story in listUsers) {
            val list = ListStoryItem(
                story.photoUrl,
                story.createdAt,
                story.name,
                story.description,
                story.lon,
                story.id,
                story.lat
            )
            stories.add(list)
        }
        val adapter = ListStoryAdapter(stories)
        binding?.rvStory?.adapter = adapter
    }

    private fun goToLoginActivity() {
        val intent = LoginActivity.loginActivityIntent(this)
        startActivity(intent)
        finish()
    }

    private fun goToCreateStoryActivity() {
        val intent = CreateStoryActivity.createStoryActivityIntent(this)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
    }

    companion object {
        fun mainActivityIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}