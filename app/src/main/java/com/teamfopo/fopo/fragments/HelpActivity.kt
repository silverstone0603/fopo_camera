package com.teamfopo.fopo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.teamfopo.fopo.R

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
        var viewHelp: View
        viewHelp = inflater.inflate(R.layout.content_help, container, false)

        initFragment(viewHelp)

        return viewHelp
    }

    fun initFragment(view: View){
        var webHelp = view.findViewById<WebView>(R.id.webHelp)
        webHelp.loadUrl("http://106.10.51.32/app/help")
        webHelp.settings.javaScriptEnabled = true
    }
}