package com.teamfopo.fopo.module

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.google.android.gms.location.*
import com.teamfopo.fopo.MainActivity

class modLocation(actMain: MainActivity){
    private var actMain: MainActivity ?= null
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback ?= null
    private var fusedLocationClient: FusedLocationProviderClient ?= null

    init{
        this.actMain = actMain
        locationRequest = LocationRequest.create()
        locationRequest!!.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60 * 1000
        }

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for((i, location) in it.locations.withIndex()) {
                        Log.d(TAG, "#$i ${location.latitude} , ${location.longitude}")
                    }
                }
            }
        }
        // fusedLocationClient!!.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.myLooper())
    }

    private fun initLocation() {
        if (ActivityCompat.checkSelfPermission(actMain!!.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(actMain!!.applicationContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(actMain!!.applicationContext)
        fusedLocationClient!!.lastLocation
            .addOnSuccessListener { location ->
                if(location == null) {
                    Log.e(TAG, "location get fail")
                } else {
                    Log.d(TAG, "${location.latitude} , ${location.longitude}")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "location error is ${it.message}")
                it.printStackTrace()
            }
    }
    /*
    override fun onPause() {
        this.onPause()
        fusedLocationClient!!.removeLocationUpdates(locationCallback)
    }
    */
}