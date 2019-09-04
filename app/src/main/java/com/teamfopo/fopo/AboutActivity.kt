package com.teamfopo.fopo

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import com.teamfopo.fopo.module.modKeyboardUtils
import kotlinx.android.synthetic.main.activity_auth.*

class AboutActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var modKeyboardUtils: modKeyboardUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        modKeyboardUtils = modKeyboardUtils(window,
            onShowKeyboard = { keyboardHeight ->
                sv_root.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })

        //뒤로가기 클릭
        val backClick = findViewById(R.id.btnBack) as ImageButton

        backClick.setOnClickListener {
            //startActivity(Intent(this,FopozoneActivity::class.java))
            finish()
        }

    }

    override fun onDestroy() {
        modKeyboardUtils.detachKeyboardListeners()
        super.onDestroy()
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
        super.onBackPressed()
        Log.e("!!!", "onBackPressed : finish, killProcess")
        finish()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //
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

}