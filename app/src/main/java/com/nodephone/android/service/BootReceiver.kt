package com.nodephone.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nodephone.android.core.server.NodePhoneServerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var serverManager: NodePhoneServerManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (serverManager.serverStatus.value.autoStartOnBoot) {
                NodePhoneService.startService(context)
            }
        }
    }
}
