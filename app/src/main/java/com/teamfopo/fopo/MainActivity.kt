package com.teamfopo.fopo

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import com.teamfopo.fopo.fragments.*
import com.teamfopo.fopo.module.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    companion object {
        var modService: modService? = null
        lateinit var mMenu: Menu // For ActionBar MenuIcon Control

        lateinit var mContext: Context
    }

    var serviceIntent: Intent? = null
    var dataMemberVO: modSysData? = null

    val actHome = HomeActivity()
    val actCamera = CameraActivity()
    val actFopomap = FopomapActivity()
    val actSetting = SettingActivity()
    val actHelp = HelpActivity()
    val actFriendAdd = FriendAddActivity()

    var bundleMain: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        var navView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        supportActionBar?.title = "카메라"
        navView.setCheckedItem(R.id.nav_camera)
        setFragment(actCamera)

        modService = modService() // 서비스클래스 객체 만듦

        mContext = this.applicationContext
        //PassportActivity.pContext = PassportActivity.pContext

         modNotificator.showNotification(false,true, "사진 동기화 완료", "FOPO 앨범의 사진을 자동으로 동기화 하였습니다.")
        // modNotificator.CancelNotification()

        // Snackbar.make(toolbar,"[FOPO] 로그인합니다.",Snackbar.LENGTH_SHORT).show()

        if (FOPOService.serviceIntent == null) {
            serviceIntent = Intent(this, FOPOService::class.java)
            startService(serviceIntent)
            Toast.makeText(applicationContext, "FOPO 동기화 서비스가 시작됩니다.", Toast.LENGTH_LONG).show()

        } else {
            serviceIntent = FOPOService.serviceIntent //getInstance().getApplication();
            // Toast.makeText(applicationContext, "FOPO 동기화 서비스가 이미 동작하고 있습니다.", Toast.LENGTH_LONG).show()
        }

        var dbms = modDBMS(this)
        var dataMemberVO = dbms.getMember()
        // Toast.makeText(applicationContext, "${dataMemberVO!!.mem_nick}님 안녕하세요!", Toast.LENGTH_LONG).show()
        
        // 사용자 이름 표시
        val headerView = navView.getHeaderView(0)
        val txtNavNick = headerView.findViewById<View>(R.id.txtNavNick) as TextView
        txtNavNick.text = "${dataMemberVO!!.mem_nick}님, 안녕하세요!"
    }

    override fun onClick(v: View?) {
        Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        /*
        when (v?.id) {
            R.id.btnMap -> {
                println("Test Message")
            }else -> {
                println("Test Message")
            }
        }*/
    }

    /*
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //var actionBar = supportActionBar

        //actionBar!!.setDisplayShowHomeEnabled(false)
        //getMenuInflater().inflate(R.menu.main, menu)
        menuInflater.inflate(R.menu.main, menu)
        mMenu = menu

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_friend_add -> {
                var mFriendAdd = mMenu.findItem(R.id.action_friend_add)
                //var mFriendSearch = mMenu.findItem(R.id.action_friend_search)
                mFriendAdd.setVisible(false)
                //mFriendSearch.setVisible(true)

                supportActionBar?.title = "친구 추가"
                makeText(this@MainActivity, "친구추가를 선택했습니다.", LENGTH_SHORT).show()

                actFopomap.setChildFragment(actFriendAdd)
            }
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        /*
        val mgrFragment = supportFragmentManager.beginTransaction()
        val actHome = HomeActivity()
        val actCamera = CameraActivity()
        val actFopomap = FopomapActivity()
        val actSetting = SettingActivity()
        val actHelp = HelpActivity()
        */

        when (item.itemId) {
            R.id.nav_home -> {
                supportActionBar?.title = "모아보기"
                makeText(this@MainActivity, "모아보기를 선택했습니다.", LENGTH_SHORT).show()

                setFragment(actHome)
            }
            R.id.nav_camera -> {
                supportActionBar?.title = "카메라"
                makeText(this@MainActivity, "카메라를 선택했습니다.", LENGTH_SHORT).show()

                setFragment(actCamera)
            }
            R.id.nav_fopomap -> {
                supportActionBar?.title = "포포맵"
                makeText(this@MainActivity, "포포맵을 선택했습니다.", LENGTH_SHORT).show()

                setFragment(actFopomap, "map")

                //FopomapActivity().setChildFragment(MapActivity())
                // setFragment(actFopozone)

            }
            R.id.nav_setting -> {
                supportActionBar?.title = "환경 설정"
                makeText(this@MainActivity, "환경 설정을 선택했습니다.", LENGTH_SHORT).show()

                setFragment(actSetting)
            }
            R.id.nav_help -> {
                supportActionBar?.title = "도움말"
                makeText(this@MainActivity, "도움말을 선택했습니다.", LENGTH_SHORT).show()

                setFragment(actHelp)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun setFragment(fragment: Fragment) {
        //현재 보여지고 있는 프래그먼트를 가져옵니다.
        val mgrFragment = supportFragmentManager
        val getTopFragment = mgrFragment.findFragmentById(R.id.fraMain)

        mgrFragment.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            .replace(R.id.fraMain, fragment)
            .addToBackStack(null)
            .commit()
        println("현재 다음 프레그먼트가 선택 되어 있습니다 : "+mgrFragment.toString())
    }

    fun setFragment(fragment: Fragment, movement: String = "", zone_no: String = "") {
        var bundle = Bundle()
        bundle.putString("movement", "$movement")
        bundle.putString("zone_no", "$zone_no")
        fragment.arguments = bundle

        setFragment(fragment)
    }


    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private var pressedTime: Long = 0

    // 리스너 생성
    interface OnBackPressedListener {
        fun onBack()
    }

    // 리스너 객체 생성
    private var mBackListener: OnBackPressedListener? = null

    // 리스너 설정 메소드
    fun setOnBackPressedListener(listener: OnBackPressedListener) {
        mBackListener = listener
    }

    // 뒤로가기 버튼을 눌렀을 때의 오버라이드 메소드
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // super.onBackPressed()
            // 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
            if (mBackListener != null) {
                mBackListener!!.onBack()
                Log.e("!!!", "Listener is not null")
                // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
                // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
            } else {
                Log.e("!!!", "Listener is null")
                if (pressedTime == 0L) {
                    Snackbar.make(
                        toolbar,
                        " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG
                    ).show()
                    pressedTime = System.currentTimeMillis()
                } else {
                    val seconds = (System.currentTimeMillis() - pressedTime).toInt()

                    if (seconds > 2000) {
                        Snackbar.make(
                            toolbar,
                            " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG
                        ).show()
                        pressedTime = 0
                    } else {
                        //super.onBackPressed()
                        //onDestroy()
                        Log.e("!!!", "onBackPressed : finish, killProcess")
                        finish()
                        //android.os.Process.killProcess(android.os.Process.myPid())
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Get a support ActionBar corresponding to this toolbar
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(false)
        //
        if (hasFocus) {
            showSystemUI()
        }
    }

    fun showSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView

        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun setActionBarTitle(title: String) {
        supportActionBar!!.title = title
    }

    fun setSnackbar(title: String) {
        //navView.setCheckedItem(R.id.nav_fopomap)
        Snackbar.make(
            toolbar,
            "$title", Snackbar.LENGTH_LONG
        ).show()
    }

    fun setTestGOGO() {
        //navView.setCheckedItem(R.id.nav_fopomap)
        setFragment(actFopomap)
    }

    fun CloseKeyBoard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.currentFocus.windowToken, 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (serviceIntent != null) {
            stopService(serviceIntent)
            serviceIntent = null
        }
    }
}
