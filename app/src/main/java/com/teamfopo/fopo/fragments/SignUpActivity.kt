package com.teamfopo.fopo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.teamfopo.fopo.AuthActivity
import com.teamfopo.fopo.R
import com.teamfopo.fopo.module.modMemProcess
import kotlinx.android.synthetic.main.content_signup.*

class SignUpActivity : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): Fragment {
            var fb: SignUpActivity = SignUpActivity()
            return fb
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.content_camera)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var viewRoot: View = inflater!!.inflate(R.layout.content_signup, container, false)
        var btnGo: Button = viewRoot.findViewById(R.id.signUpButton) as Button
        btnGo.setOnClickListener(this)

        //initFragment()

        return viewRoot
    }

    //fun initFragment(){  }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.signUpButton -> {
                var mem_id = idInput.text.toString()
                var mem_pw = passwordInput.text.toString()
                var mem_checkpw = passwordCheeckInput.text.toString()
                var mem_nick = nickName.text.toString()
                var mem_gender = "2"

                //성별 체크 남자=1 여자=2
                if (radio_men.isChecked) mem_gender = "1"
                else mem_gender = "2"

                if ( mem_id.equals("")) {
                    Toast.makeText(context, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return
                } else if ( mem_pw.equals("") ) {
                    Toast.makeText(context, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return
                } else if ( !mem_pw.equals(mem_checkpw) ) {
                    Toast.makeText(context, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    return
                } else if ( mem_nick.equals("") ) {
                    Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                    return
                }

                var getMemInfo = modMemProcess().signUp()
                var memInfo = getMemInfo.execute("$mem_id", "$mem_pw", "$mem_nick", "$mem_gender").get()

                when (memInfo) {
                    "true" -> {
                        Toast.makeText(context, "FOPO 계정이 생성되었습니다.", Toast.LENGTH_SHORT).show()
                        (activity as AuthActivity).setScreen(0)
                    }
                    "false" -> {
                        Toast.makeText(context, "계정 생성 실패! 고객지원 센터에 문의하세요", Toast.LENGTH_SHORT).show()
                    }
                    "exist_id" -> {
                        Toast.makeText(context, "중복된 ID 입니다.", Toast.LENGTH_SHORT).show()
                    }
                    "exist_nick" -> {
                        Toast.makeText(context, "중복된 닉네임 입니다.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "알수없는 오류! 고객지원 센터에 문의하세요", Toast.LENGTH_SHORT).show()
                    }
                } //when 끝부분

            }else -> {   } //회원가입 버튼 if문 끝
        } //when(v?.id) 끝부분
    } //onClick끝부분
}