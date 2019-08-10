package com.teamfopo.fopo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.teamfopo.fopo.R

class SettingActivity : Fragment() {

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
        var viewRoot: View
        viewRoot = inflater.inflate(R.layout.content_setting, container, false)

        return viewRoot
    }

    fun initFragment(view: View){

    }
}