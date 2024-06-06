package com.example.testlag

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.testlag.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isLocationPermissionGranted()

        if (isGeoDisabled(this)) {
            startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        binding.btnClick.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }

    fun isGeoDisabled(context: Context): Boolean {
        val mLocationManager: LocationManager =
            context.getSystemService(LOCATION_SERVICE) as LocationManager
        val mIsGPSEnabled: Boolean =
            mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val mIsNetworkEnabled: Boolean =
            mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val mIsGeoDisabled = !mIsGPSEnabled && !mIsNetworkEnabled
        return mIsGeoDisabled
    }

    private fun isLocationPermissionGranted(): Boolean {
        return if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            false
        } else {
            true
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
            1
        )
    }
}