package com.teamfopo.fopo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.teamfopo.fopo.module.modAuthProcess
import kotlinx.android.synthetic.main.activity_auth.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.teamfopo.fopo.module.modDBMS
import com.teamfopo.fopo.module.modService
import com.teamfopo.fopo.module.modSysData

class AuthActivity : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(): Fragment {
            var fb: AuthActivity = AuthActivity()
            return fb
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.content_camera)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        var viewRoot: View = inflater!!.inflate(R.layout.activity_auth, container, false)
        var btnGoToLogin: Button = viewRoot.findViewById(R.id.loginButton) as Button
        var btnGoToSignUp: Button = viewRoot.findViewById(R.id.registerButon) as Button

        btnGoToLogin.setOnClickListener(this)
        btnGoToSignUp.setOnClickListener(this)

        //initFragment()

        return viewRoot
    }

    //fun initFragment(){  }

    override fun onClick(v: View?) {
        when (v?.id) {

            //로그인 버튼
            R.id.loginButton -> {

                var mem_id = idInput.text.toString()
                var mem_pw = passwordInput.text.toString()


                if (mem_id != null && mem_pw != null) {
                    var getLoginInfo = modAuthProcess().login()
                    var LoginInfo = getLoginInfo.execute("$mem_id", "$mem_pw").get()

                    // 시간을 받아서 String으로 변환
                    val current = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    val logindate = current.format(formatter)
                    val lastdate = current.format(formatter)

                    when (LoginInfo.status) {
                        "succeed" -> {
                            // 받아오는값: status, token (db에서 토큰이 만들어짐)
                            // SQLite에서 이미 토큰이있는지 확인..
                            // 없으면 만들고 로그인성공..

                            val dbms = modDBMS(context!!)
                            val modSysData = modSysData()

                            modSysData.mem_id = mem_id
                            modSysData.token = LoginInfo.token
                            modSysData.logindate = logindate
                            modSysData.lastdate = lastdate

                            dbms.addUser(modSysData)
                            Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()

                            MainActivity.modService!!.mem_id = mem_id
                            MainActivity.modService!!.token = LoginInfo.token
                            MainActivity.modService!!.logindate = logindate
                            MainActivity.modService!!.lastdate = lastdate
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
                }else{
                    Toast.makeText(context, "ID와PASSWD를 공백없이 입력하세요!", Toast.LENGTH_SHORT).show()
                }
            } //R.id.loginButton 끝부분

            //회원가입 버튼
            R.id.registerButon -> {

            }

        } //when(v?.id) 끝부분
    } //onClick끝부분

}