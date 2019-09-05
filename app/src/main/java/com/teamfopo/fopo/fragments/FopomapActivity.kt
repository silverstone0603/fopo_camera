package com.teamfopo.fopo.fragments

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.teamfopo.fopo.*
import com.teamfopo.fopo.MainActivity.Companion.mMenu
import kotlinx.android.synthetic.main.activity_main.*


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

        if (arguments != null) {
            var movement = arguments!!.getString("movement")
            var zone_no = arguments!!.getString("zone_no")

            Log.d("TESTTEST", "$movement")

            if (movement.equals("map")) {
                setChildFragment(MapActivity())
            } else if ( movement.equals("fopozone") ) {
                setChildFragment(FopozoneActivity(), "$zone_no")
            }
        }

    initFragment()

    return viewRoot
}

    fun initFragment(){
        /*
            AR Fragment 생성 부분
        */

        //setChildFragment(MapActivity())
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
        OnNavigationItemSelectedListener { item ->
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

    fun setChildFragment(child: Fragment, zone_no: String) {
        var bundle = Bundle()
        bundle.putString("zone_no", "$zone_no")
        child.arguments = bundle

        setChildFragment(child)
    }

    fun testtest(zone_no: String) {
        (activity as MainActivity).nav_view.setCheckedItem(R.id.nav_fopomap)
        (activity as MainActivity).supportActionBar?.title = "포포맵"
        (activity as MainActivity).setFragment(FopomapActivity())

        var fragment: Fragment = FopozoneActivity()
        var bundle = Bundle()
        bundle.putString("zone_no", "$zone_no")
        fragment.arguments = bundle
        var fragmentManager = fragmentManager
        var fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fopomap_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}