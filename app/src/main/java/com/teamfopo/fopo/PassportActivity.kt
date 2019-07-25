package com.teamfopo.fopo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.teamfopo.fopo.module.FOPOService.Companion.serviceIntent
import kotlinx.android.synthetic.main.activity_passport.*

class PassportActivity:AppCompatActivity() {
    companion object {
        var sess_verify: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //val dbms = modDBMS(this)
        //var dataMemberVO = dbms.getMember()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport)

        init()
    }

    override fun onNewIntent(intent: Intent?) {
        init()
        super.onNewIntent(intent)
    }

    fun init() {
        var i = getIntent()
        var getString = i.getIntExtra("sess_verify", 0)

        numAuth.setText(getString.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceIntent != null) {
            stopService(serviceIntent)
            serviceIntent = null
        }
    }
}