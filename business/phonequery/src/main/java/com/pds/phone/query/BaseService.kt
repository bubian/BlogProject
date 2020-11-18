package com.pds.phone.query

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import com.pds.splugin.model.InterfaceService

open class BaseService : Service(), InterfaceService {
    private var that: Service? = null

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun attach(proxyService: Service?) {
        that = proxyService
    }

    override fun onCreate() {
        Log.d(TAG, "$TAG onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "$TAG onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "$TAG onDestroy"
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d(TAG, "$TAG onConfigurationChanged")
    }

    override fun onLowMemory() {
        Log.d(TAG, "$TAG onLowMemory")
    }

    override fun onTrimMemory(level: Int) {
        Log.d(TAG, "$TAG onTrimMemory")
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "$TAG onUnbind")
        return false
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "$TAG onRebind")
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d(TAG, "$TAG onTaskRemoved")
    }

    companion object {
        private const val TAG = "pds"
    }
}