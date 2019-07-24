package com.teamfopo.fopo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.teamfopo.fopo.module.modDBMS

class PassportActivity:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val dbms = modDBMS(this)
        var dataMemberVO = dbms.getMember()


        super.onCreate(savedInstanceState)
    }
}