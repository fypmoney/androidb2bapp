package com.fypmoney.listener


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fypmoney.connectivity.network.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

const val requestCodeGPSAddress = 102

class LocationListenerClass(
    val context: Activity,
    var getCurrentLocationListener: GetCurrentLocationListener
) :
    LocationListener {
    private var locationManager: LocationManager? = null

    fun permissions() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), requestCodeGPSAddress
            )
        } else {
            try {
                if (NetworkUtil.isNetworkAvailable()) {
                    locationManager =
                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                    if (locationManager != null) {
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            5000,
                            0f,
                            this
                        )
                    }
                } else {
                    getCurrentLocationListener.getCurrentLocation(
                        false,
                        0.0,
                        0.0
                    )


                 //   Utility.showToast(context.getString(R.string.no_internet_try_again))
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }


    override fun onLocationChanged(location: Location) {
        // DialogUtils.dismissProgressDialog()
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)

            GlobalScope.launch(Dispatchers.IO) {
                Log.d("dmddjdjjdj",location.toString())
                (context as AppCompatActivity).runOnUiThread {
                    getCurrentLocationListener.getCurrentLocation(
                        true,
                        location.latitude,
                        location.longitude
                    )
                    //  tvLocation.setText("${addresses[0].locality}, ${addresses[0].adminArea}, ${addresses[0].countryCode}")
                }
            }
        } catch (e: Exception) {
            Log.e("tag", "Received an exception", e)
        }
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }


    override fun onProviderEnabled(provider: String) {
        super.onProviderEnabled(provider)


    }

    override fun onProviderDisabled(provider: String) {

        //  DialogUtils.dismissProgressDialog()
     /*  if (!Utility.canGetLocation(context)) {
            Utility.showDialog(context,
                "Turn on location services to allow \"Fyp Money App\" to determine your location.",
                "Settings",
                "Cancel",
                true,
                { _, _ -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
                { dialog, _ -> dialog.cancel() })
        }*/
        getCurrentLocationListener.getCurrentLocation(
            true,
            0.0,
            0.0
        )


    }

    interface GetCurrentLocationListener {
        fun getCurrentLocation(
            isInternetConnected: Boolean? = false,
            latitude: Double,
            Longitude: Double
        )

    }
}