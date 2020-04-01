package com.rafakob.nsdhelper

import android.net.nsd.NsdManager.RegistrationListener
import android.net.nsd.NsdServiceInfo

class NsdListenerRegistration(private val mNsdHelper: NsdHelper) : RegistrationListener {
    override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        mNsdHelper.logError("Service registration failed!", errorCode, ERROR_SOURCE)
    }

    override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
        mNsdHelper.logError("Service unregistration failed!", errorCode, ERROR_SOURCE)
    }

    override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
        mNsdHelper.logMsg("Registered -> " + serviceInfo.serviceName)
        mNsdHelper.onRegistered(serviceInfo.serviceName)
    }

    override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
        mNsdHelper.logMsg("Unregistered -> " + serviceInfo.serviceName)
    }

    companion object {
        private const val ERROR_SOURCE = "android.net.nsd.NsdManager.RegistrationListener"
    }

}