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

        var btnMap: Button = viewRoot.findViewById(R.id.btnMap) as Button
        btnMap.setOnClickListener(this)
        var btnWrite: Button = viewRoot.findViewById(R.id.btnWrite) as Button
        btnWrite.setOnClickListener(this)
        var btnFriend: Button = viewRoot.findViewById(R.id.btnFriend) as Button
        btnFriend.setOnClickListener(this)

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
            R.id.btnMap -> {
                Toast.makeText(context, "포포맵", Toast.LENGTH_LONG).show()
                setChildFragment(MapActivity())
            }R.id.btnWrite -> {
                Toast.makeText(context,"글쓰기", Toast.LENGTH_LONG).show()
            }R.id.btnFriend -> {
                Toast.makeText(context,"친구 목록", Toast.LENGTH_LONG).show()
            }else -> {
                Toast.makeText(context,"Test", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setChildFragment(child: Fragment) {
        val childFt = childFragmentManager.beginTransaction()

        if (!child.isAdded) {
            childFt.replace(R.id.fopomap_container, child)
            childFt.addToBackStack(null)
            childFt.commit()
        }
    }

}