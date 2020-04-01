package com.rafakob.nsdhelper

import java.util.*

class NsdType {
    companion object {
        const val DEFAULT_HTTP = "_http._tcp"
        const val DEFAULT_PRINTER = "_ipp._tcp"

        private const val PROTOCOL_PREFIX = "_"
        private const val PROTOCOL_COMBINER = "."

        fun create(protocol: String, transportLayer: String = TransportLayer.TCP.toString()): String {
            var mutProtocol = protocol.toLowerCase(Locale.getDefault())
            var mutTransportLayer = transportLayer.toLowerCase(Locale.getDefault())

            if (!protocol.startsWith(PROTOCOL_PREFIX)) {
                mutProtocol = PROTOCOL_PREFIX + mutProtocol
            }

            if (!transportLayer.startsWith(PROTOCOL_PREFIX)) {
                mutTransportLayer = PROTOCOL_PREFIX + mutTransportLayer
            }
            return mutProtocol + PROTOCOL_COMBINER + mutTransportLayer
        }
    }
}