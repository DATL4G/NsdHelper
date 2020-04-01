package com.rafakob.nsdhelper

import android.net.nsd.NsdServiceInfo
import android.os.Parcel
import android.os.Parcelable
import java.net.InetAddress

open class NsdService : Parcelable {

    val name: String?
    val type: String?
    private val hostIp: String?
    private val hostName: String?
    private val port: Int
    private val host: InetAddress?

    constructor(nsdServiceInfo: NsdServiceInfo) {
        name = nsdServiceInfo.serviceName
        type = nsdServiceInfo.serviceType
        hostIp = if (nsdServiceInfo.host == null) null else nsdServiceInfo.host.hostAddress
        hostName = if (nsdServiceInfo.host == null) null else nsdServiceInfo.host.hostName
        port = nsdServiceInfo.port
        host = nsdServiceInfo.host
    }

    constructor(name: String, type: String, hostIp: String, hostName: String, port: Int, host: InetAddress) {
        this.name = name
        this.type = type
        this.hostIp = hostIp
        this.hostName = hostName
        this.port = port
        this.host = host
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        type = `in`.readString()
        hostIp = `in`.readString()
        hostName = `in`.readString()
        port = `in`.readInt()
        host = `in`.readSerializable() as InetAddress?
    }

    override fun toString(): String {
        return "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", hostIp='" + hostIp + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as NsdService
        if (port != that.port) return false
        if (if (name != null) name != that.name else that.name != null) return false
        if (if (type != null) type != that.type else that.type != null) return false
        if (if (hostIp != null) hostIp != that.hostIp else that.hostIp != null) return false
        return if (hostName != null) hostName == that.hostName else that.hostName == null
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + (hostIp?.hashCode() ?: 0)
        result = 31 * result + (hostName?.hashCode() ?: 0)
        result = 31 * result + port
        return result
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(type)
        dest.writeString(hostIp)
        dest.writeString(hostName)
        dest.writeInt(port)
        dest.writeSerializable(host)
    }

    companion object {
        val CREATOR: Parcelable.Creator<NsdService> = object : Parcelable.Creator<NsdService> {
            override fun createFromParcel(source: Parcel): NsdService? {
                return NsdService(source)
            }

            override fun newArray(size: Int): Array<NsdService?> {
                return arrayOfNulls(size)
            }
        }
    }
}