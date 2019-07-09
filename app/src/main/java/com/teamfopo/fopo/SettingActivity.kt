package com.teamfopo.fopo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class SettingActivity : Fragment(), View.OnClickListener {

    private val TAG = "MainActivity"
    private var trackableGestureDetector: GestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.content_camera)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //var viewCamera: View
        //viewCamera = inflater.inflate(R.layout.content_setting, container, false)
        var viewRoot: View = inflater.inflate(R.layout.content_setting, container, false)

        /** 갑자기 와있노 이거 ㅅㅂ -기원 7.10
        var btnFopozones: Button = viewRoot.findViewById(R.id.btnFopozones) as Button
        btnFopozones.setOnClickListener(this)
        */
        initFragment()

        return viewRoot
    }

    fun initFragment(){
        /*
            AR Fragment 생성 부분
        */


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            /** 7.10 오류나서 주석 - 기원
            R.id.btnFopozones -> {

            }
            */
        }
    }
}