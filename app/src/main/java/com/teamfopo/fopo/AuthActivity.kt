package com.teamfopo.fopo

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.teamfopo.fopo.fragments.*
import com.teamfopo.fopo.module.modKeyboardUtils
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var modKeyboardUtils: modKeyboardUtils

    private var nowFragment: Int = 0
    private val actLogin = LoginActivity()
    private val actSignUp = SignUpActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        modKeyboardUtils = modKeyboardUtils(window,
            onShowKeyboard = { keyboardHeight ->
                sv_root.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })

        setScreen(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        modKeyboardUtils.detachKeyboardListeners()
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
        if (nowFragment != 0) {
            setScreen(0)
            Toast.makeText(this, "로그인 화면으로 되돌아갑니다.", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(
                        this,
                        "한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG
                    ).show()
                    pressedTime = System.currentTimeMillis()
                } else {
                    val seconds = (System.currentTimeMillis() - pressedTime).toInt()

                    if (seconds > 2000) {
                        Toast.makeText(
                            this,
                            "한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG
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
        /*
        if (hasFocus) {
            showSystemUI()
        }
        */
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
            .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
            .replace(R.id.fraLogin, fragment)
            .addToBackStack(null)
            // .disallowAddToBackStack()
            .commit()

        println("현재 다음 프레그먼트가 선택 되어 있습니다 : $nowFragment")
    }

    fun setScreen(num: Int) {
        //navView.setCheckedItem(R.id.nav_fopomap)
        when(num){
            0 ->{ // 로그인 화면
                nowFragment = 0
                setFragment(actLogin)
            } 1 ->{ // 회원가입 화면
                nowFragment = 1
                setFragment(actSignUp)
            }
        }
    }

    fun setMain(){
        val nextIntent = Intent(this, MainActivity::class.java)
        startActivity(nextIntent)
        finish()
    }

}