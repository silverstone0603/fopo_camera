package com.teamfopo.fopo

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
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import com.teamfopo.fopo.module.modService
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    companion object {
        var modService: modService ?= null
    }

    val actHome = HomeActivity()
    val actCamera = CameraActivity()
    val actFopomap = FopomapActivity()
    val actSetting = SettingActivity()
    val actHelp = HelpActivity()

    val actFopozone = FopozoneActivity()
    // val actView = ViewActivity()

    var bundleMain: Bundle? = null

    //회원가입 엑티비티
    val actRegister = SignUpActivity()
    //로그인 엑티비티
    val actAuth = AuthActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        /*
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            make(view, "FOPO Test :(", LENGTH_LONG)
            .setAction("Action", null).show()
        }
        */

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        navView.setCheckedItem(R.id.nav_camera)
        //setFragment(actCamera)

        //회원가입 먼저 실행하기.
        //setFragment(actRegister)
        //로그인 먼저 실행하기.
        setFragment(actAuth)

        initLayoutAction()

        modService = modService() // 서비스클래스 객체만들고..

        Snackbar.make(toolbar,"[FOPO] 테스트 계정으로 로그인합니다.",Snackbar.LENGTH_SHORT).show()
    }

    private fun initLayoutAction(){
        /*
        bundleMain!!.putString("userId","userIdValue")
        actHome.arguments = bundleMain
        */

        // 글쓰기 버튼
        /*
        imageButtonWrite.setOnClickListener {
            setFragment(actHome)
        }
        */

        /*
        val btnGo: Button = findViewById(R.id.btnGoGo)
        btnGo.setOnClickListener(this)
        */
    }

    override fun onClick(v: View?) {
        Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        when (v?.id) {
            R.id.btnGoGo -> {
                println("Test Message")
            }else -> {
                println("Test Message")
            }
        }
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
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
                Snackbar.make(toolbar,"모아보기를 눌렀습니다.",Snackbar.LENGTH_SHORT).show()

                setFragment(actHome)
            }
            R.id.nav_camera -> {
                supportActionBar?.title = "카메라"
                makeText(this@MainActivity, "카메라를 선택했습니다.", LENGTH_SHORT).show()
                Snackbar.make(toolbar,"카메라를 눌렀습니다.",Snackbar.LENGTH_SHORT).show()

                setFragment(actCamera)
            }
            R.id.nav_fopomap -> {
                supportActionBar?.title = "포포맵"
                makeText(this@MainActivity, "포포맵을 선택했습니다.", LENGTH_SHORT).show()
                Snackbar.make(toolbar,"포포맵을 눌렀습니다.",Snackbar.LENGTH_SHORT).show()

                setFragment(actFopozone)
                // setFragment(actFopomap)

            }
            R.id.nav_setting -> {
                supportActionBar?.title = "환경 설정"
                makeText(this@MainActivity, "환경 설정을 선택했습니다.", LENGTH_SHORT).show()
                Snackbar.make(toolbar,"환경 설정을 눌렀습니다.",Snackbar.LENGTH_SHORT).show()

                setFragment(actSetting)
            }
            R.id.nav_help -> {
                supportActionBar?.title = "도움말"
                makeText(this@MainActivity, "도움말을 선택했습니다.", LENGTH_SHORT).show()
                Snackbar.make(toolbar,"도움말을 눌렀습니다.",Snackbar.LENGTH_SHORT).show()

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
            .commit()
        println("현재 다음 프레그먼트가 선택 되어 있습니다 : "+mgrFragment.toString())
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
                        super.onBackPressed()
                        Log.e("!!!", "onBackPressed : finish, killProcess")
                        finish()
                        android.os.Process.killProcess(android.os.Process.myPid())
                    }
                }
            }
        }
    }


}
