package com.rafakob.nsdhelper

import android.os.CountDownTimer

internal class DiscoveryTimer(private val mOnTimeoutListener: OnTimeoutListener, seconds: Long) {
    private var mTimer: CountDownTimer?

    fun start() {
        mTimer?.start()
    }

    fun cancel() {
        mTimer?.cancel()
    }

    fun reset() {
        mTimer?.cancel()
        mTimer?.start()
    }

    fun timeout(seconds: Long) {
        mTimer?.cancel()
        mTimer = null
        mTimer = createTimer(seconds)
    }

    private fun createTimer(seconds: Long): CountDownTimer {
        return object : CountDownTimer(1000 * seconds, 1000 * seconds) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                mOnTimeoutListener.onNsdDiscoveryTimeout()
            }
        }
    }

    internal interface OnTimeoutListener {
        fun onNsdDiscoveryTimeout()
    }

    init {
        mTimer = createTimer(seconds)
    }
}