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
import com.fypmoney.R
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


    override fun onStart() {
        super.onStart()
        /*if (checkUpdate.value!!) {
            checkForAppUpdate()
        }*/
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

            }
        }


        appUpdateManager.appUpdateInfo.addOnFailureListener { appUpdateInfo: Exception ->
            Log.i(TAG,"app update failed $appUpdateInfo")
            checkUpdate.value = false

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_UPDATE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this,
                        "App Update failed, please try again on the next app launch.",
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