package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.model.Story

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: StoryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Mengambil data cerita dari intent
        val storyList: List<Story>? = intent.getParcelableArrayListExtra<Story>("STORY_LIST")?.toList()

        storyList?.let { stories ->
            // Anda bisa menyimpan data cerita dan memprosesnya setelah peta siap
            mapFragment.getMapAsync { googleMap ->
                mMap = googleMap
                displayStoriesOnMap(stories)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Menambahkan marker untuk setiap cerita setelah peta siap
        val storyList: List<Story>? = intent.getParcelableArrayListExtra<Story>("STORY_LIST")?.toList()
        storyList?.let { displayStoriesOnMap(it) }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        // Ambil data dari ViewModel
        viewModel.stories.observe(this) { stories ->
            stories?.let { displayStoriesOnMap(it) }
        }

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        getMyLocation()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun displayStoriesOnMap(stories: List<Story>) {
        // Inisialisasi LatLngBounds.Builder
        val builder = LatLngBounds.Builder()

        // Variabel untuk menyimpan jumlah marker yang sudah ditambahkan pada posisi tertentu
        val markerOffsets = mutableMapOf<String, Int>()

        // Tambahkan marker untuk setiap cerita
        for (story in stories) {
            val lat = story.lat
            val lon = story.lon
            if (lat != null && lon != null) {
                var location = LatLng(lat, lon)

                // Membuat string kunci untuk lokasi ini
                val locationKey = "$lat,$lon"

                // Cek apakah sudah ada marker yang ditambahkan di lokasi ini
                val offset = markerOffsets.getOrDefault(locationKey, 0)

                // Jika ada marker yang sudah ada, beri sedikit pergeseran
                if (offset > 0) {
                    location = LatLng(lat + offset * 0.0001, lon + offset * 0.0001)
                }

                // Tambahkan marker dengan posisi yang mungkin sudah digeser
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(story.name ?: "Unknown")
                        .snippet(story.description ?: "No description")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                )

                // Update offset untuk lokasi ini
                markerOffsets[locationKey] = offset + 1

                // Tambahkan lokasi marker ke LatLngBounds.Builder
                builder.include(location)
            }
        }

        // Tentukan batas (LatLngBounds) yang mencakup semua marker
        val bounds = builder.build()

        // Hitung padding (batas dari peta agar marker tidak terlalu dekat dengan tepi)
        val padding = 100 // Dalam piksel

        // Sesuaikan kamera agar mencakup seluruh bounds
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    }



    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}