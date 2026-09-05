package com.nodephone.android.core.mdns

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.nodephone.android.core.network.NetworkUtils
import com.nodephone.android.core.pairing.DeviceIdentityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MdnsBroadcaster @Inject constructor(
    @ApplicationContext private val context: Context,
    private val identityManager: DeviceIdentityManager,
    private val networkUtils: NetworkUtils
) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as? NsdManager
    private var registrationListener: NsdManager.RegistrationListener? = null
    @Volatile private var isRegistered = false

    fun startBroadcasting(port: Int = 8080) {
        if (isRegistered || nsdManager == null) return

        val serviceInfo = NsdServiceInfo().apply {
            serviceName = identityManager.deviceName
            serviceType = "_nodephone._tcp."
            setPort(port)
            setAttribute("version", identityManager.nodePhoneVersion)
            setAttribute("deviceId", identityManager.deviceId)
            setAttribute("ip", networkUtils.getLocalIpAddress())
            setAttribute("available", "true")
        }

        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                isRegistered = true
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                isRegistered = false
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                isRegistered = false
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                isRegistered = false
            }
        }

        try {
            nsdManager.registerService(
                serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                registrationListener
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopBroadcasting() {
        if (!isRegistered || nsdManager == null) return
        try {
            registrationListener?.let {
                nsdManager.unregisterService(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isRegistered = false
            registrationListener = null
        }
    }
}
