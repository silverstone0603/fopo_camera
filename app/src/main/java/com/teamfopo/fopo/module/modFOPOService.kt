package com.teamfopo.fopo.module

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.SystemClock
import android.widget.Toast
import com.teamfopo.fopo.module.FOPOService.Companion.Context_FOPOService
import com.teamfopo.fopo.module.FOPOService.Companion.Toast_Notice
import java.util.*

class FOPOService : Service() {
    companion object {
        const val Toast_Notice: Int = 1

        var serviceIntent: Intent? = null
        var Context_FOPOService: Application? = null

        var dataMemberVO: modSysData? = null
    }

    var TestThread: Thread? = ThreadTest()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceIntent = intent
        Context_FOPOService = application

        val dbms = modDBMS(Context_FOPOService!!.applicationContext)
        dataMemberVO = dbms.getMember()

        TestThread?.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceIntent = null
        setAlarmTimer()

        Thread.currentThread().interrupt()

        if (TestThread != null) {
            TestThread?.interrupt()
            TestThread = null
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

class ThreadTest: Thread() {
    override fun run() {
        var run = true

        while (run) {
            try {
                SystemClock.sleep(5000)
                // handler.sendEmptyMessage(Toast_Notice) // 메인스레드에서의 작업 필요시 핸들러 호출.. (UI갱신, 토스트 등)
                            // 참조 -> sendMessage메소드를 사용하면 정수가아닌 msg정보를 핸들러도 전송가능
                modNotificator.showNotification(false, "쓰레드테스트", "5초마다 울립니다 ㅋㅋ",0, Context_FOPOService!!.applicationContext)
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
}