package com.teamfopo.fopo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.teamfopo.fopo.AuthActivity
import com.teamfopo.fopo.R
import com.teamfopo.fopo.module.modAuthProcess
import com.teamfopo.fopo.module.modDBMS
import com.teamfopo.fopo.module.modSysData
import kotlinx.android.synthetic.main.content_login.*
import kotlinx.android.synthetic.main.content_signup.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class LoginActivity : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(): Fragment {
            var fb: LoginActivity = LoginActivity()
            return fb
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.content_camera)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var viewRoot: View = inflater!!.inflate(R.layout.content_login, container, false)
        var btnLogin: Button = viewRoot.findViewById(R.id.btnLogin) as Button
        var txtRegister: TextView = viewRoot.findViewById(R.id.txtRegister) as TextView

        btnLogin.setOnClickListener(this)
        txtRegister.setOnClickListener(this)

        //initFragment()

        return viewRoot
    }

    //fun initFragment(){  }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                var mem_id = editUserID.text.toString()
                var mem_pw = editPassword.text.toString()

                if ( mem_id.equals("") && mem_pw.equals("") ) {
                    Toast.makeText(context, "아이디와 비밀번호를 공백없이 입력하세요!", Toast.LENGTH_SHORT).show()
                    return
                }

                var formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA)
                var currentTime = Date()
                val lastlogin = formatter.format(currentTime)

                var getLoginInfo = modAuthProcess().login()
                var LoginInfo = getLoginInfo.execute("$mem_id", "$mem_pw").get()

                when (LoginInfo.status) {
                    "succeed" -> {
                        // 받아오는값: status, token (db에서 토큰이 만들어짐)
                        // SQLite에서 이미 토큰이있는지 확인..
                        // 없으면 만들고 로그인성공..

                        val dbms = modDBMS(context!!)
                        val sysdata = modSysData()

                        sysdata.mem_id = mem_id
                        sysdata.token = LoginInfo.token
                        sysdata.mem_no = LoginInfo.mem_no
                        sysdata.mem_nick = LoginInfo.mem_nick
                        sysdata.lastlogin = lastlogin

                        dbms.addMember(sysdata)
                        Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()

                        (activity as AuthActivity).setMain()
                    }

                    "exist_session" -> {
                        Toast.makeText(context, "다른 기기에서 이미 로그인이 되어있습니다.", Toast.LENGTH_SHORT).show()
                        // 받아오는값: status ( db에 이미 토큰이 있음..) 비정상적인 접근..
                    }

                    "failed" -> {
                        Toast.makeText(context, "아이디 또는 비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "알 수 없는 오류가 발생 했습니다. FOPO팀에게 문의해주세요.", Toast.LENGTH_SHORT).show()
                    }
                } //when 끝부분
            } //R.id.loginButton 끝부분


            //회원가입 버튼
            R.id.txtRegister -> {
                (activity as AuthActivity).setScreen(1)
            }

        } //when(v?.id) 끝부분
    } //onClick끝부분
}