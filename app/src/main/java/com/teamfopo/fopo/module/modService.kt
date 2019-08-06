package com.teamfopo.fopo.module

import android.app.Service
import android.content.Intent
import android.os.IBinder

class modService: Service() {
    var mem_id=""
    var mem_nick=""
    var token=""
    var logindate=""
    var lastdate=""

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}