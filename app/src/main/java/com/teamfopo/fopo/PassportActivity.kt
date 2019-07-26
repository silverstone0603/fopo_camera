package com.teamfopo.fopo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.teamfopo.fopo.module.FOPOService.Companion.serviceIntent
import kotlinx.android.synthetic.main.activity_passport.*

class PassportActivity:AppCompatActivity(), View.OnClickListener {
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

    fun init() {
        var i = getIntent()
        var getString = i.getIntExtra("sess_verify", 0)

        numAuth.setText(getString.toString())

        var btnPassportCancel = findViewById<Button>(R.id.btnPassportCancel)
        btnPassportCancel.setOnClickListener(this)
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