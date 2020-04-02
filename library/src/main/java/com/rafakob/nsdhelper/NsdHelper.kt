package com.rafakob.nsdhelper

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import com.rafakob.nsdhelper.DiscoveryTimer.OnTimeoutListener
import java.io.IOException
import java.net.ServerSocket

class NsdHelper : OnTimeoutListener {
    private val mNsdManager: NsdManager
    var nsdListener: NsdListener? = null
    private var mRegistered = false
    private var mRegistrationListener: NsdListenerRegistration? = null
    var registeredService: NsdService? = null
        private set
    var registeredServiceInfo = NsdServiceInfo()
        private set
    var isDiscoveryRunning = false
        private set
    var discoveryTimeout: Long = 15
        private set
    var discoveryServiceType: String? = null
        private set
    var discoveryServiceName: String? = null
        private set
    private var mDiscoveryListener: NsdListenerDiscovery? = null
    private var mDiscoveryTimer: DiscoveryTimer
    var isAutoResolveEnabled = true
    private var mResolveQueue: ResolveQueue
    var isLogEnabled = false

    constructor(nsdManager: NsdManager) {
        mNsdManager = nsdManager
        mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        mResolveQueue = ResolveQueue(this)
    }

    constructor(nsdManager: NsdManager, nsdListener: NsdListener) {
        mNsdManager = nsdManager
        this.nsdListener = nsdListener
        mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        mResolveQueue = ResolveQueue(this)
    }

    constructor(context: Context) {
        mNsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        mResolveQueue = ResolveQueue(this)
    }

    constructor(context: Context, nsdListener: NsdListener) {
        mNsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        this.nsdListener = nsdListener
        mDiscoveryTimer = DiscoveryTimer(this, discoveryTimeout)
        mResolveQueue = ResolveQueue(this)
    }

    fun setDiscoveryTimeout(seconds: Int) {
        require(seconds >= 0) { "Timeout has to be greater or equal 0!" }
        discoveryTimeout = if (seconds == 0) Int.MAX_VALUE.toLong() else seconds.toLong()
        mDiscoveryTimer.timeout(discoveryTimeout)
    }

    @JvmOverloads
    fun registerService(desiredServiceName: String, serviceType: String, port: Int = findAvailablePort()) {
        if (port == 0) return
        registeredServiceInfo = NsdServiceInfo()
        registeredServiceInfo.serviceName = desiredServiceName
        registeredServiceInfo.serviceType = serviceType
        registeredServiceInfo.port = port
        mRegistrationListener = NsdListenerRegistration(this)
        mNsdManager.registerService(registeredServiceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener)
    }

    fun unregisterService() {
        if (mRegistered) {
            mRegistered = false
            mNsdManager.unregisterService(mRegistrationListener)
        }
    }

    @JvmOverloads
    fun startDiscovery(serviceType: String, serviceName: String? = null) {
        if (!isDiscoveryRunning) {
            isDiscoveryRunning = true
            mDiscoveryTimer.start()
            discoveryServiceType = serviceType
            discoveryServiceName = serviceName
            mDiscoveryListener = NsdListenerDiscovery(this)
            mNsdManager.discoverServices(discoveryServiceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener)
        }
    }

    fun stopDiscovery() {
        if (isDiscoveryRunning) {
            isDiscoveryRunning = false
            mDiscoveryTimer.cancel()
            mNsdManager.stopServiceDiscovery(mDiscoveryListener)
            nsdListener?.onNsdDiscoveryFinished()
        }
    }

    fun resolveService(nsdService: NsdService) {
        val serviceInfo = NsdServiceInfo()
        serviceInfo.serviceName = nsdService.name
        serviceInfo.serviceType = nsdService.type
        mResolveQueue.enqueue(serviceInfo)
    }

    fun resolveService(serviceInfo: NsdServiceInfo) {
        mNsdManager.resolveService(serviceInfo, NsdListenerResolve(this))
    }

    private fun findAvailablePort(): Int {
        val socket: ServerSocket
        return try {
            socket = ServerSocket(0)
            val port = socket.localPort
            socket.close()
            port
        } catch (e: IOException) {
            logError("Couldn't assign port to your service.", 0, "java.net.ServerSocket")
            e.printStackTrace()
            0
        }
    }

    fun onRegistered(serviceName: String?) {
        mRegistered = true
        registeredServiceInfo.serviceName = serviceName
        registeredService = NsdService(registeredServiceInfo)
        nsdListener?.onNsdRegistered(registeredService)
    }

    fun onNsdServiceFound(foundService: NsdServiceInfo) {
        mDiscoveryTimer.reset()
        nsdListener?.onNsdServiceFound(NsdService(foundService))
        if (isAutoResolveEnabled) mResolveQueue.enqueue(foundService)
    }

    fun onNsdServiceResolved(resolvedService: NsdServiceInfo) {
        mResolveQueue.next()
        mDiscoveryTimer.reset()
        nsdListener?.onNsdServiceResolved(NsdService(resolvedService))
    }

    fun onNsdServiceLost(lostService: NsdServiceInfo) {
        nsdListener?.onNsdServiceLost(NsdService(lostService))
    }

    fun logMsg(msg: String) {
        if (isLogEnabled) Log.d(TAG, msg)
    }

    fun logError(errorMessage: String?, errorCode: Int, errorSource: String?) {
        Log.e(TAG, errorMessage ?: errorCode.toString())
        nsdListener?.onNsdError(errorMessage, errorCode, errorSource)
    }

    override fun onNsdDiscoveryTimeout() {
        stopDiscovery()
    }

    companion object {
        private const val TAG = "NsdHelper"
    }
}