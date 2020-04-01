package com.rafakob.nsdhelper

interface NsdListener {

    fun onNsdRegistered(registeredService: NsdService?)

    fun onNsdDiscoveryFinished()

    fun onNsdServiceFound(foundService: NsdService?)

    fun onNsdServiceResolved(resolvedService: NsdService?)

    fun onNsdServiceLost(lostService: NsdService?)

    fun onNsdError(errorMessage: String?, errorCode: Int, errorSource: String?)
}