package com.rafakob.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rafakob.nsdhelper.NsdHelper
import com.rafakob.nsdhelper.NsdListener
import com.rafakob.nsdhelper.NsdService
import com.rafakob.nsdhelper.NsdType

class MainActivity : AppCompatActivity(), NsdListener {
    private lateinit var nsdHelper: NsdHelper
    private lateinit var nsdServiceType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nsdServiceType = NsdType.create(getString(R.string.app_name))

        nsdHelper = NsdHelper(this, this).apply {
            isLogEnabled = true
            registerService(getString(R.string.app_name), nsdServiceType)
        }

        nsdHelper.startDiscovery(nsdServiceType)
    }

    override fun onStop() {
        super.onStop()
        nsdHelper.stopDiscovery()
        nsdHelper.unregisterService()
    }

    override fun onNsdRegistered(registeredService: NsdService?) {}
    override fun onNsdDiscoveryFinished() {}
    override fun onNsdServiceFound(foundService: NsdService?) {}
    override fun onNsdServiceResolved(resolvedService: NsdService?) {}
    override fun onNsdServiceLost(lostService: NsdService?) {}
    override fun onNsdError(errorMessage: String?, errorCode: Int, errorSource: String?) {}
}