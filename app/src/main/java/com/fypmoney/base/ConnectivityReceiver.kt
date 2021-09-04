package com.fypmoney.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager


class ConnectivityReceiver(var connectivityReceiverListener: ConnectivityReceiverListener) :
    BroadcastReceiver() {

    fun setFypConnectivityReceiverListener(connectivityReceiverListener: ConnectivityReceiverListener) {
        this@ConnectivityReceiver.connectivityReceiverListener = connectivityReceiverListener
    }

    override fun onReceive(context: Context, arg1: Intent) {
        connectivityReceiverListener.onNetworkConnectionChanged(isConnected(context))
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }


    private fun isConnected(context: Context): Boolean {
        val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }


}