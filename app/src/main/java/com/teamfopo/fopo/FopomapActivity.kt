package com.teamfopo.fopo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class FopomapActivity : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): Fragment {
            var fb: FopomapActivity = FopomapActivity()
            return fb
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.content_camera)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var viewRoot: View = inflater!!.inflate(R.layout.content_fopomap, container, false)

        var btnGo: Button = viewRoot.findViewById(R.id.btnGoGo) as Button
        btnGo.setOnClickListener(this)


    initFragment()

    return viewRoot
}

    fun initFragment(){
        /*
            AR Fragment 생성 부분
        */

        // 이동 클릭
        // startActivity(Intent(this,MainActivity::class.java))
        // setFragment(actFopozone)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnGoGo -> {
                Toast.makeText(context,"test", Toast.LENGTH_LONG).show()
            }else -> {
            }
        }
    }

}