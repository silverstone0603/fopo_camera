package com.teamfopo.fopo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.teamfopo.fopo.module.modDBMS
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbms = modDBMS(this)
        var dataMemberVO = dbms.getMember()

        /*** TEST CODE (날짜계산이 정상적으로 되는지?) 날짜바꿔주기 귀찮아서 몇일지나면 테스트해봄 ㅎㅎ ***/
        if (!dataMemberVO.lastlogin.equals("")) {
            System.out.println("가나다라: " + dataMemberVO.lastlogin)
            System.out.println("TESTTEST: " + calcDaysBetweenNowAndLastDate(dataMemberVO.lastlogin))
        }

        if ( dataMemberVO.token.equals("") || ((!dataMemberVO.lastlogin.equals("")) && calcDaysBetweenNowAndLastDate(dataMemberVO.lastlogin) > 5 )) {
            var intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        } else {
            // sess_lastdate, lastdate 업데이트 필요.
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        finish()
    }

    private fun calcDaysBetweenNowAndLastDate(lastdate: String): Long { // 현재날짜와 인자값의 날짜의 차이 일수 반환.
        var formatter = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA)
        var currentDate = Date()

        var lastLoginDate = formatter.parse(lastdate)

        val calDate = currentDate.getTime() - lastLoginDate.getTime()
        var calDateDays = calDate / ( 24 * 60 * 60 * 1000 )

        calDateDays = Math.abs(calDateDays)

        return calDateDays
    }
}