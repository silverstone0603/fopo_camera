package com.teamfopo.fopo.module

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.teamfopo.fopo.MainActivity
import com.teamfopo.fopo.R

class RestartService: Service() {
    override fun onCreate() { super.onCreate() }

    override fun onDestroy() { super.onDestroy() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val builder = NotificationCompat.Builder(this, "default")
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setContentTitle(null)
        builder.setContentText(null)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        builder.setContentIntent(pendingIntent)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    "default",
                    "기본 채널",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val notification = builder.build()
        startForeground(9, notification)

        /////////////////////////////////////////////////////////////////////
        val intent = Intent(this, FOPOService::class.java)
        startService(intent)

        stopForeground(true)
        stopSelf()

        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? { return null }

    override fun onUnbind(intent: Intent?): Boolean { return super.onUnbind(intent) }
}