package com.teamfopo.fopo

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.app_bar_main.*

class AuthActivity : AppCompatActivity(), View.OnClickListener{

    val actLogin = LoginActivity()
    val actSignUp = SignUpActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        setFragment(actLogin)
    }

    // 뒤로가기 버튼 입력시간이 담길 long 객체
    private var pressedTime: Long = 0

    // 리스너 객체 생성
    private var mBackListener: MainActivity.OnBackPressedListener? = null

    // 리스너 설정 메소드
    fun setOnBackPressedListener(listener: MainActivity.OnBackPressedListener) {
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Get a support ActionBar corresponding to this toolbar
        val ab = supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
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

    override fun onClick(v: View?) {

    } //onClick끝부분

    fun setFragment(fragment: Fragment) {
        //현재 보여지고 있는 프래그먼트를 가져옵니다.
        val mgrFragment = supportFragmentManager
        val getTopFragment = mgrFragment.findFragmentById(R.id.fraLogin)
        mgrFragment.beginTransaction()
            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
            .replace(R.id.fraMain, fragment)
            .commit()
        println("현재 다음 프레그먼트가 선택 되어 있습니다 : "+mgrFragment.toString())
    }

    fun setSnackbar(title: String) {
        //navView.setCheckedItem(R.id.nav_fopomap)
        Snackbar.make(
            toolbar,
            "$title", Snackbar.LENGTH_LONG
        ).show()
    }

    fun setScreen(num: Int) {
        //navView.setCheckedItem(R.id.nav_fopomap)
        when(num){
            0 ->{ // 로그인 화면
                setFragment(actLogin)
            } 1 ->{ // 회원가입 화면
                setFragment(actSignUp)
        }
        }

    }

}