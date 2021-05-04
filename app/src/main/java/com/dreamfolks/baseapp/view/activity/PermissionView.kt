package com.dreamfolks.baseapp.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.dreamfolks.baseapp.BR
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.base.BaseActivity
import com.dreamfolks.baseapp.databinding.ViewEnterOtpBinding
import com.dreamfolks.baseapp.databinding.ViewPermissionBinding
import com.dreamfolks.baseapp.databinding.ViewSplashBinding
import com.dreamfolks.baseapp.util.AppConstants.LOCATION_PERMISSION_REQUEST_CODE
import com.dreamfolks.baseapp.util.PermissionUtils
import com.dreamfolks.baseapp.util.Utility
import com.dreamfolks.baseapp.viewmodel.PermissionViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.view_permission.*


/*
* This class is used for show app logo and check user logged in or not
* */
class PermissionView : BaseActivity<ViewPermissionBinding, PermissionViewModel>() {
    private lateinit var mViewModel: PermissionViewModel
    private lateinit var mViewBinding: ViewPermissionBinding

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.view_permission
    }

    override fun getViewModel(): PermissionViewModel {
        mViewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)
        return mViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewDataBinding()
        onGrantPermissionClicked()
        mViewBinding.activity = this
        setObserver()
    }

    /**
     * Create this method for observe the viewModel fields
     */
    private fun setObserver() {


    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        location_permission.text= "Lattitude"+location.latitude.toString()+"   " + "longitude " + location.longitude.toString()
                      //  Utility.showToast("Lattitude " + location.latitude.toString() + "   " + "longitude " + location.longitude.toString())
                        /*latTextView.text = location.latitude.toString()
                        lngTextView.text = location.longitude.toString()
           */
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Location is required",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun onGrantPermissionClicked() {
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

    }
}



