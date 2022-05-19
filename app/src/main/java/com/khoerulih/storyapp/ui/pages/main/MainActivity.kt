package com.khoerulih.storyapp.ui.pages.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.khoerulih.storyapp.data.remote.responses.ListStoryItem
import com.khoerulih.storyapp.databinding.ActivityMainBinding
import com.khoerulih.storyapp.ui.pages.ListStoryAdapter
import com.khoerulih.storyapp.ui.pages.LoadingStateAdapter
import com.khoerulih.storyapp.ui.pages.ViewModelFactory
import com.khoerulih.storyapp.ui.pages.createstory.CreateStoryActivity
import com.khoerulih.storyapp.ui.pages.login.LoginActivity
import com.khoerulih.storyapp.ui.pages.maps.MapsActivity
import com.khoerulih.storyapp.utils.SettingPreferences
import com.khoerulih.storyapp.utils.SettingViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application, pref)
        )[SettingViewModel::class.java]

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(application, pref)
        )[MainViewModel::class.java]

        binding?.rvStory?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(this)
        binding?.rvStory?.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding?.rvStory?.addItemDecoration(itemDecoration)

        settingViewModel.getSession().observe(this) { token ->
            if (token.isNullOrEmpty()) {
                goToLoginActivity()
            }
        }

        binding?.fabAdd?.setOnClickListener {
            goToCreateStoryActivity()
        }

        setListData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.maps -> {
                goToMapsActivity()
                return true
            }
            R.id.logout -> {
                val pref = SettingPreferences.getInstance(dataStore)
                val settingViewModel =
                    ViewModelProvider(
                        this,
                        ViewModelFactory.getInstance(application, pref)
                    )[SettingViewModel::class.java]
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.logout))
                    .setMessage(getString(R.string.logout_confirmation))
                    .setPositiveButton(getString(R.string.yes)) { _, _ ->
                        val intent = LoginActivity.loginActivityIntent(this)
                        startActivity(intent)
                        settingViewModel.deleteSession()
                        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_LONG)
                            .show()
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

    private fun setListData() {
        val adapter = ListStoryAdapter()
        binding?.rvStory?.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.listStory.observe(this) { story ->
            adapter.submitData(lifecycle, story)
        }

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

    private fun goToMapsActivity() {
        val intent = MapsActivity.mapsActivityIntent(this)
        startActivity(intent)
    }

    companion object {
        fun mainActivityIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}