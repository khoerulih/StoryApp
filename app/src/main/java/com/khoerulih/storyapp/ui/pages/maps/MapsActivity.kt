package com.khoerulih.storyapp.ui.pages.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.khoerulih.storyapp.R
import com.khoerulih.storyapp.data.remote.responses.ListStoryItem
import com.khoerulih.storyapp.databinding.ActivityMapsBinding
import com.khoerulih.storyapp.ui.pages.ViewModelFactory
import com.khoerulih.storyapp.ui.pages.login.LoginActivity
import com.khoerulih.storyapp.utils.SettingPreferences
import com.khoerulih.storyapp.utils.SettingViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var listStory: ArrayList<ListStoryItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        mapsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MapsViewModel::class.java]

        settingViewModel.getSession().observe(this) { token ->
            if (token.isNullOrEmpty()) {
                goToLoginActivity()
            } else {
                mapsViewModel.getAllStoryWithLocation(token)
            }
        }

        mapsViewModel.listStoryWithLocation.observe(this){ story ->
            setDataMaps(story)

            if(story.isNullOrEmpty()){
                Toast.makeText(this, getString(R.string.data_empty), Toast.LENGTH_SHORT).show()
            }
            mapFragment.getMapAsync(this)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        for (story in listStory) {
                if (story.lat != null && story.lon != null){
                    val storyLocation = LatLng(story.lat, story.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(storyLocation)
                            .title(story.name)
                            .snippet(story.description)
                    )
                }
            }

        val indonesiaLocation = LatLng(-6.200000, 106.816666)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesiaLocation, 5f))

        getMyLocation()
        setMapStyle()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun setDataMaps(listStories: List<ListStoryItem>) {
        listStory = ArrayList()
        for (story in listStories) {
            val list = ListStoryItem(
                story.photoUrl,
                story.createdAt,
                story.name,
                story.description,
                story.lon,
                story.id,
                story.lat
            )
            listStory.add(list)
        }
    }

    private fun goToLoginActivity() {
        val intent = LoginActivity.loginActivityIntent(this)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val TAG = "MapsActivity"
        const val EXTRA_STORIES = "extra_stories"

        fun mapsActivityIntent(context: Context): Intent {
            return Intent(context, MapsActivity::class.java)
        }
    }
}