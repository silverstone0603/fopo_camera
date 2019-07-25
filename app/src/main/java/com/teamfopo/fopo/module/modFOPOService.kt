package com.teamfopo.fopo.module

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.widget.Toast
import com.teamfopo.fopo.MainActivity
import com.teamfopo.fopo.PassportActivity
import com.teamfopo.fopo.module.FOPOService.Companion.Context_FOPOService
import com.teamfopo.fopo.module.FOPOService.Companion.Toast_Notice
import com.teamfopo.fopo.module.FOPOService.Companion.dataMemberVO
import java.util.*

class FOPOService : Service() {
    companion object {
        const val Toast_Notice: Int = 1

        var serviceIntent: Intent? = null
        var Context_FOPOService: Application? = null

        var dataMemberVO: modSysData? = null
    }

    var AuthThread: Thread? = AuthThread()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceIntent = intent
        Context_FOPOService = application

        val dbms = modDBMS(Context_FOPOService!!.applicationContext)
        dataMemberVO = dbms.getMember()

        AuthThread?.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceIntent = null
        setAlarmTimer()

        Thread.currentThread().interrupt()

        if (AuthThread != null) {
            AuthThread?.interrupt()
            AuthThread = null
        }
    }

    override fun onCreate() { super.onCreate() }

    override fun onBind(intent: Intent?): IBinder? { return null }

    override fun onUnbind(intent: Intent?): Boolean { return super.onUnbind(intent) }

    protected fun setAlarmTimer() {
        val c = Calendar.getInstance()
        c.timeInMillis = System.currentTimeMillis()
        c.add(Calendar.SECOND, 1)

        val intent = Intent(this, AlarmRecever::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, 0)

        val mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, sender)
    }
}

class AuthThread: Thread() {
    override fun run() {
        var run = true

        while (run) {
            try {
                SystemClock.sleep(5000)

                var sess_token = dataMemberVO!!.token

                var webLoginAuth = modAuthProcess().web_auth()
                var tempInfo = webLoginAuth.execute("$sess_token").get()

                if ( tempInfo.status.equals("exist_auth")) {
                    var sess_no = tempInfo.sess_no
                    
                    var testim = modAuthProcess().testim()
                    testim.execute("$sess_no") // 추후 예외처리 필요
                    
                    val notificationIntent = Intent(Context_FOPOService!!.applicationContext, PassportActivity::class.java)
                    notificationIntent.putExtra("sess_no", tempInfo.sess_no)
                    notificationIntent.putExtra("sess_verify", tempInfo.sess_verify)

                    modNotificator.showNotification(false, true, "FOPO 로그인 인증", "다른 기기에서 로그인을 요청하였습니다.",0, Context_FOPOService!!.applicationContext, notificationIntent)
                } else {

                }
            } catch (e: InterruptedException) {
                run = false
                e.printStackTrace()
            }
        }
    }
}




/*class ThreadTest: Thread() {
    override fun run() {
        var run = true

        while (run) {
            try {
                SystemClock.sleep(5000)
                //handler.sendEmptyMessage(Toast_Notice) // 메인스레드에서의 작업 필요시 핸들러 호출.. (UI갱신, 토스트 등)
                            // 참조 -> sendMessage메소드를 사용하면 정수가아닌 msg정보를 핸들러도 전송가능


                //Robolectric.buildActivity(PassportActivity::class.java).create().get()
                //modNotificator.showNotification(false, "쓰레드테스트", "5초마다 울립니다 ㅋㅋ",0, Context_FOPOService!!.applicationContext)
            } catch (e: InterruptedException) {
                run = false
                e.printStackTrace()
            }
        }
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                Toast_Notice -> {
                    Toast.makeText(Context_FOPOService, "사용자스레드에서 메인스레드사용 테스트", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}*/