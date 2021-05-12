package com.fypmoney.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.fypmoney.util.Utility
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Utility.showToast("user entered in the geofence")

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

           /* // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(
                this,
                geofenceTransition,
                triggeringGeofences
            )*/

            // Send notification and log the transition details.
        //    sendNotification(geofenceTransitionDetails)
         //   Log.i(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            Log.e(TAG, "error")
        }
    }
}