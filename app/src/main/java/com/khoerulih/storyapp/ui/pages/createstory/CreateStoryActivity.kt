package com.khoerulih.storyapp.ui.pages.createstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.khoerulih.storyapp.R
import com.khoerulih.storyapp.databinding.ActivityCreateStoryBinding
import com.khoerulih.storyapp.ui.pages.ViewModelFactory
import com.khoerulih.storyapp.ui.pages.login.LoginActivity
import com.khoerulih.storyapp.ui.pages.main.MainActivity
import com.khoerulih.storyapp.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CreateStoryActivity : AppCompatActivity() {
    private var _binding: ActivityCreateStoryBinding? = null
    private val binding get() = _binding

    private var getFile: File? = null
    private var token: String? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val pref = SettingPreferences.getInstance(dataStore)
        val settingViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]

        val createStoryViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[CreateStoryViewModel::class.java]

        settingViewModel.getSession().observe(this) { session ->
            if (session.isNullOrEmpty()) {
                val intent = LoginActivity.loginActivityIntent(this)
                startActivity(intent)
                finish()
            } else {
                token = session
            }
        }

        binding?.btnCamera?.setOnClickListener {
            startCameraX()
        }

        binding?.btnGallery?.setOnClickListener {
            startGallery()
        }

        binding?.btnCreate?.setOnClickListener {
            if (getFile != null) {
                if(!binding?.etDescription?.text.isNullOrBlank()){
                    val file = reduceFileImage(getFile as File)
                    val description = binding?.etDescription?.text.toString().toRequestBody("text/plain".toMediaType())
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile
                    )
                    val lat = currentLocation.latitude.toFloat()
                    val lon = currentLocation.longitude.toFloat()

                    createStoryViewModel.createStory(token, imageMultipart, description, lat, lon)
                    showLoading(true)
                } else {
                    Toast.makeText(this, getString(R.string.description_story_error), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, getString(R.string.upload_empty), Toast.LENGTH_SHORT).show()
            }
        }

        createStoryViewModel.isError.observe(this){ status ->
            showLoading(false)
            setUploadStatus(status)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getMyLastLocation()

        supportActionBar?.title = "Create Story"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {}
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                } else {
                    Toast.makeText(
                        this@CreateStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.pemission_error),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra(EXTRA_PICTURE) as File
            val isBackCamera = it.data?.getBooleanExtra(IS_BACK_CAMERA, true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )
            binding?.ivPreview?.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateStoryActivity)
            getFile = myFile
            binding?.ivPreview?.setImageURI(selectedImg)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
    }

    private fun setUploadStatus(error: Boolean) {
        if (!error) {
            Toast.makeText(this, getString(R.string.upload_story_success), Toast.LENGTH_SHORT).show()
            goToMainActivity()
        } else {
            Toast.makeText(this, getString(R.string.upload_story_failed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMainActivity() {
        val intent = MainActivity.mainActivityIntent(this)
        startActivity(intent)
        finishAffinity()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        const val CAMERA_X_RESULT = 200
        const val EXTRA_PICTURE = "picture"
        const val IS_BACK_CAMERA = "isBackCamera"

        fun createStoryActivityIntent(context: Context): Intent {
            return Intent(context, CreateStoryActivity::class.java)
        }
    }
}