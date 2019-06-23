package com.teamfopo.fopo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.teamfopo.fopo.module.modDBMS
import com.teamfopo.fopo.module.modSysData

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbms = modDBMS(this)
        //var abc = modSysData()

        //dbms.addUser(abc)
        var abc = dbms.getMember()

        if ( !abc.mem_id.equals("") ) {
            var intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        } else {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        startActivity(intent)
        finish()
    }
}