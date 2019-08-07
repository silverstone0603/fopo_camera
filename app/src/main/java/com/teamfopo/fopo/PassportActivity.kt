package com.teamfopo.fopo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.google.a.b.a.a.a.e
import com.teamfopo.fopo.module.FOPOService
import com.teamfopo.fopo.module.FOPOService.Companion.serviceIntent
import com.teamfopo.fopo.module.modAuthProcess
import com.teamfopo.fopo.module.modNotificator
import kotlinx.android.synthetic.main.activity_passport.*

class PassportActivity:AppCompatActivity(), View.OnClickListener {
    companion object {
        var sess_verify: Int = 0
        var sess_no: Int = 0

        lateinit var PassportActivity: Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //val dbms = modDBMS(this)
        //var dataMemberVO = dbms.getMember()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport)

        init()
    }

    fun init() {
        var i = getIntent()
        var getVerifyString = i.getIntExtra("sess_verify", 0)
        sess_no = i.getIntExtra("sess_no", 0)

        numAuth.setText(getVerifyString.toString())

        var btnPassportCancel = findViewById<Button>(R.id.btnPassportCancel)
        btnPassportCancel.setOnClickListener(this)

        PassportActivity = this

        AuthCheckThread().start()
    }

    override fun onNewIntent(intent: Intent?) {
        init()
        super.onNewIntent(intent)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnPassportCancel -> {
                finish()
            } else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceIntent != null) {
            stopService(serviceIntent)
            serviceIntent = null
        }
    }
}

class AuthCheckThread: Thread() {
    override fun run() {
        var run = true

        while (run) {
            try {
                SystemClock.sleep(5000)

                var auth_check = modAuthProcess().auth_check()
                var result = auth_check.execute("${PassportActivity.sess_no}").get()

                if ( result.status.equals("succeed") ) {
                    PassportActivity.PassportActivity.finish()
                    this.interrupt()
                }

            } catch (e: InterruptedException) {
                run = false
                e.printStackTrace()
            }
        }
    }
}