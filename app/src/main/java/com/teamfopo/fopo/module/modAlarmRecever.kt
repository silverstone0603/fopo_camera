package com.teamfopo.fopo.module

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class AlarmRecever: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(context, RestartService::class.java)
            context!!.startForegroundService(intent)
        } else {
            val intent = Intent(context, FOPOService::class.java)
            context!!.startService(intent)
        }
    }
}