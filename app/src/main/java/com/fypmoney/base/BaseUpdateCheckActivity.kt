package com.fypmoney.base

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.fyp.trackr.models.TrackrEvent
import com.fyp.trackr.models.TrackrField
import com.fyp.trackr.models.trackr
import com.fyp.trackr.services.TrackrServices
import com.fypmoney.R
import com.fypmoney.application.PockketApplication
import com.fypmoney.util.SharedPrefUtils
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import io.reactivex.Observable

open class BaseUpdateCheckActivity : AppCompatActivity() {
    private val TAG = BaseUpdateCheckActivity::class.java.simpleName
    protected var updateType: Int = -1
    protected var checkUpdate = MutableLiveData(false);
    private val appUpdateManager: AppUpdateManager by lazy { AppUpdateManagerFactory.create(this) }
    private val appUpdatedListener: InstallStateUpdatedListener by lazy {
        object : InstallStateUpdatedListener {
            override fun onStateUpdate(installState: InstallState) {
                when {
                    installState.installStatus() == InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                    installState.installStatus() == InstallStatus.INSTALLED -> appUpdateManager.unregisterListener(this)
                    else -> Log.d("InstallUpdatedListener:", "${installState.installStatus()}")
                }
            }
        }
    }





     fun checkForAppUpdate() {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(updateType)) {
                // Request the update.
                try {
                    if (updateType == AppUpdateType.FLEXIBLE) {
                        appUpdateManager.registerListener(appUpdatedListener)
                        trackr {
                            it.name = TrackrEvent.flex_update_popup_is_shown
                        }
                    }else{
                        trackr {
                            it.name = TrackrEvent.Force_update_screen_is_shown
                        }
                    }
                    updateType.let {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                it,
                                this,
                                APP_UPDATE_REQUEST_CODE
                        )
                    }
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                    checkUpdate.value = false

                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                Log.e(TAG,"No app update")
                checkUpdate.value = false
                trackr {
                    it.name = TrackrEvent.no_app_update_available
                }
            }
        }


        appUpdateManager.appUpdateInfo.addOnFailureListener { appUpdateInfo: Exception ->
            Log.i(TAG,"app update failed $appUpdateInfo")
            checkUpdate.value = false
            trackr {
                it.name = TrackrEvent.force_update_failed
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this,
                        getString(R.string.app_update_failed_please_try_again_on_the_next_app_launch),
                        Toast.LENGTH_SHORT)
                        .show()
                if (updateType == 1) {
                    checkForAppUpdate()
                }
            }
        }
    }

    private fun popupSnackbarForCompleteUpdate() {
        if (this.isFinishing) {
            return
        }
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, which: Int ->
                    run {
                        appUpdateManager.completeUpdate()
                        dialog.dismiss()
                    }
                }
                .setMessage(resources.getString(R.string.an_update_has_just_been_downloaded))


        dialogBuilder.setTitle(resources.getString(R.string.app_update_title))

        val dialog: Dialog = dialogBuilder.create()
        if(dialog.isShowing){
            return
        }
        trackr {
            it.name = TrackrEvent.force_update_completed
        }
        dialog.show()
    }


    override fun onResume() {
        super.onResume()
        appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->

                    // If the update is downloaded but not installed,
                    // notify the user to complete the update.
                    if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                        popupSnackbarForCompleteUpdate()
                    }
                    //Check if Immediate update is required
                    try {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                            // If an in-app update is already running, resume the update.
                            appUpdateManager.startUpdateFlowForResult(
                                    appUpdateInfo,
                                    updateType,
                                    this,
                                    APP_UPDATE_REQUEST_CODE
                            )
                        }
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                        checkUpdate.value = false
                    }
                }
    }

    companion object {
        private const val APP_UPDATE_REQUEST_CODE = 1991
    }
}