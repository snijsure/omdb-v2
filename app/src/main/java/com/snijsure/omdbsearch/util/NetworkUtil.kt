package com.snijsure.omdbsearch.util


import android.net.NetworkCapabilities
import javax.inject.Inject

class NetworkUtil @Inject constructor(private val networkCapabilities : NetworkCapabilities?) {

    fun isNetworkConnected() : Boolean {
        networkCapabilities?.let { cap ->
            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }
}