package com.dreamfolks.baseapp.view.activity

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dreamfolks.baseapp.R
import com.dreamfolks.baseapp.receivers.GeofenceBroadcastReceiver
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {
    private lateinit var geofencingClient: GeofencingClient
    private var geofenceList=ArrayList<Geofence>()

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_push_notification)
        setSupportActionBar(findViewById(R.id.toolbar))
        geofencingClient = LocationServices.getGeofencingClient(this)

        val response =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)
        if (response != ConnectionResult.SUCCESS) {
            Log.d("", "Google Play Service Not Available")
            GoogleApiAvailability.getInstance().getErrorDialog(this@MainActivity, response, 1)
                .show()
        } else {
            Log.d("", "Google play service available")
        }

       geofenceList.add(
           Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId("100")

            // Set the circular region of this geofence.
            .setCircularRegion(
                28.397577490454537,
                77.07243082140417,
               10F
            )

            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(500000)

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

            // Create the geofence.
            .build())

        geofenceList.add(
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("101")

                // Set the circular region of this geofence.
                .setCircularRegion(
                    28.320702,
                    77.340290,
                    100F
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(500000)



                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build())



        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                Log.d("bhxhbcbbcbh","geofence_added")
                // Geofences added
                // ...
            }
            addOnFailureListener {
                Log.d("bhxhbcbbcbh","geofence_failed")

                // Failed to add geofences
                // ...
            }
        }

  /*  val wv = findViewById<View>(R.id.webView) as WebView
          wv.settings.javaScriptEnabled = true
        //  wv.settings.setPluginsEnabled(true)
          val mimeType = "text/html"
          val encoding = "UTF-8"
          val html: String = getHTML()
          wv.loadDataWithBaseURL("", html, mimeType, encoding, "")*/

    }

    fun getHTML(): String {
        return """<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
img {
  border-radius: 20%;
}
</style>
</head>
<body>

 

<h2>Rounded Images</h2>

 

<img src="https://img.etimg.com/thumb/msid-81434728,width-300,imgsize-85820,,resizemode-4,quality-100/axis-agencies.jpg" alt="Avatar" style="width:200px">

 

<img src="https://img.etimg.com/thumb/msid-81434728,width-300,imgsize-85820,,resizemode-4,quality-100/axis-agencies.jpg" alt="Avatar" style="width:200px">

 

</body>
"""
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }
}