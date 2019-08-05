package com.teamfopo.fopo.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.teamfopo.fopo.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeActivity : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var viewHome: View
        viewHome = inflater.inflate(R.layout.content_home, container, false)

        // 프레그먼트 초기화
        initFragment(viewHome)

        return viewHome
    }

    fun initFragment(view: View){
        var webHome = view.findViewById<WebView>(R.id.webHome)
        webHome.loadUrl("http://106.10.51.32/app/home")
    }


}
