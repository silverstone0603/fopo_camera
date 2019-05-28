package com.teamfopo.fopo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class HelpActivity : Fragment() {

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
        var viewCamera: View
        viewCamera = inflater.inflate(R.layout.content_help, container, false)

        initFragment()

        return viewCamera
    }

    fun initFragment(){
        /*
            AR Fragment 생성 부분
        */

    }
}