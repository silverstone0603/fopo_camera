package com.teamfopo.fopo

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.teamfopo.fopo.MainActivity.Companion.mMenu


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

        var navBottomView: BottomNavigationView = viewRoot.findViewById(R.id.bottom_navigation)
        navBottomView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        /*
        var btnMap: Button = viewRoot.findViewById(R.id.btnMap) as Button
        btnMap.setOnClickListener(this)
        var btnWrite: Button = viewRoot.findViewById(R.id.btnWrite) as Button
        btnWrite.setOnClickListener(this)
        var btnFriend: Button = viewRoot.findViewById(R.id.btnFriend) as Button
        btnFriend.setOnClickListener(this)*/


    initFragment()

    return viewRoot
}

    fun initFragment(){
        /*
            AR Fragment 생성 부분
        */

        setChildFragment(MapActivity())

    }

    override fun onClick(v: View?) {
        /*
        when (v?.id) {
            R.id.btnMap -> {
                Toast.makeText(context, "포포맵", Toast.LENGTH_LONG).show()
                setChildFragment(MapActivity())
            }R.id.btnWrite -> {
                Toast.makeText(context,"글쓰기", Toast.LENGTH_LONG).show()
                val writeIntent = Intent(activity, WriteActivity::class.java)
                startActivity(writeIntent)
            }R.id.btnFriend -> {
                Toast.makeText(context,"친구 목록", Toast.LENGTH_LONG).show()
            }else -> {
                Toast.makeText(context,"Test", Toast.LENGTH_LONG).show()
            }
        }*/
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_one -> {
                    (activity as MainActivity).supportActionBar?.title = "포포맵"
                    setChildFragment(MapActivity())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_two -> {
                    setChildFragment(FriendListActivity())

                    (activity as MainActivity).supportActionBar?.title = "친구 목록"
                    var mFriendAdd = mMenu.findItem(R.id.action_friend_add)
                    mFriendAdd.setVisible(true)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_three -> {
                    val writeIntent = Intent(activity, WriteActivity::class.java)
                    startActivity(writeIntent)
                }
            }
            false
        }


    fun setChildFragment(child: Fragment) {
        val childFt = childFragmentManager.beginTransaction()

        if (!child.isAdded) {
            childFt.replace(R.id.fopomap_container, child)
            childFt.addToBackStack(null)
            childFt.commit()
        }
    }
}