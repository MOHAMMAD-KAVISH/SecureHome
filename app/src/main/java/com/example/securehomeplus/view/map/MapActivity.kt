package com.example.securehomeplus.view.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securehomeplus.R
import com.example.securehomeplus.databinding.ActivityMapBinding
import com.example.securehomeplus.data.database.entities.LocationEntity
import com.example.securehomeplus.utils.LocationUtils
import com.example.securehomeplus.viewmodel.MapViewModel
import com.google.android.material.appbar.MaterialToolbar
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import kotlin.math.roundToInt

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private val vm = MapViewModel()
    private var userLat = 0.0
    private var userLng = 0.0
    private lateinit var myLocationOverlay: MyLocationNewOverlay
    private var nearbyList = listOf<LocationEntity>()



    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            initLocationAndMap()
        } else {
            Toast.makeText(this, "Location permission required to show map.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


        // Setup map view basic
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(15.0)

        binding.fabRecenter.setOnClickListener {
            if (userLat != 0.0 && userLng != 0.0) {
                binding.mapView.controller.animateTo(GeoPoint(userLat, userLng))
            }
        }

        // bottom sheet toggle when title clicked
        binding.tvNearbyTitle.setOnClickListener {
            toggleBottomSheet()
        }

        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED -> {
                initLocationAndMap()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "Location permission needed to show your position on map.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun initLocationAndMap() {
        // Show my-location overlay using osmdroid's GPS provider
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.mapView)
        myLocationOverlay.enableMyLocation()
        binding.mapView.overlays.add(myLocationOverlay)


        Thread {
            val loc = try {
                kotlinx.coroutines.runBlocking { LocationUtils.getLastLocation(this@MapActivity) }
            } catch (e: Exception) {
                null
            }
            runOnUiThread {
                if (loc != null) {
                    userLat = loc.latitude
                    userLng = loc.longitude
                    val userPoint = GeoPoint(userLat, userLng)
                    binding.mapView.controller.setCenter(userPoint)
                    addUserMarker(userPoint)
                    loadNearbyAndMarkers(userLat, userLng)
                } else {
                    Toast.makeText(this, "Unable to get location; showing default view.", Toast.LENGTH_SHORT).show()
                    val default = GeoPoint(31.2555, 75.7058) // fallback
                    binding.mapView.controller.setCenter(default)
                    loadNearbyAndMarkers(default.latitude, default.longitude)
                }
                // set safety index
                binding.tvSafetyIndex.text = vm.getSafetyIndexText()
            }
        }.start()
    }

    private fun addUserMarker(pt: GeoPoint) {
        val marker = Marker(binding.mapView)
        marker.position = pt
        marker.title = "You are here"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.mapView.overlays.add(marker)
    }

    private fun loadNearbyAndMarkers(lat: Double, lng: Double) {
        nearbyList = vm.generateNearby(lat, lng)
        // add markers
        for (point in nearbyList) {
            val p = GeoPoint(point.latitude, point.longitude)
            val m = Marker(binding.mapView)
            m.position = p
            m.title = point.name
            m.subDescription = point.type
            m.setOnMarkerClickListener { mark, mapView ->
                // show snippet + option to get directions
                val dist = LocationUtils.distanceInMeters(userLat, userLng, point.latitude, point.longitude)
                mark.snippet = "${point.type} • ${dist.roundToInt()} m away"
                mark.showInfoWindow()
                true
            }
            binding.mapView.overlays.add(m)
        }

        // populate bottom sheet list
        binding.rvNearby.layoutManager = LinearLayoutManager(this)
        val adapter = NearbyAdapter(nearbyList, { loc -> onNearbyClicked(loc) }, { loc ->
            val d = LocationUtils.distanceInMeters(userLat, userLng, loc.latitude, loc.longitude)
            "${d.roundToInt()} m"
        })
        binding.rvNearby.adapter = adapter
        binding.bottomSheet.visibility = View.VISIBLE
    }

    private fun onNearbyClicked(loc: LocationEntity) {
        // animate to marker
        binding.mapView.controller.animateTo(GeoPoint(loc.latitude, loc.longitude))
        val uri = Uri.parse("geo:${loc.latitude},${loc.longitude}?q=${Uri.encode(loc.name)}")
        val i = Intent(Intent.ACTION_VIEW, uri)
        startActivity(i)
    }

    private fun toggleBottomSheet() {
        binding.bottomSheet.visibility = if (binding.bottomSheet.visibility == View.GONE) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
}
