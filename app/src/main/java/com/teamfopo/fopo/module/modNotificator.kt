package com.teamfopo.fopo.module

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.widget.Toast
import com.teamfopo.fopo.MainActivity
import com.teamfopo.fopo.fragments.FopomapActivity
import android.support.v4.content.ContextCompat.getSystemService
import android.R
import android.graphics.Color

// 현재 기본 기능에서 추후 필요한 기능 있을시 마다 Update 예정..
//  - > 푸시알림시 소리 안나옴, 푸시 클릭했을시 인텐트값 넘어갈때 어떻게 할것인지..
// ********************************************** 사용법 ************************************************
// modNotificator.
//     showNotification(드래그 시 삭제가능여부, 제목, 내용, [ 알림ID값(Default = 0) ], [ 스몰아이콘값(Default = R.drawable.ic_fopo_logo) ],
//                                       [ 라지아이콘값(Default = R.drawable.img_logo) ]) // 푸시알림
//     CancelNotification(알림ID값) -> 특정 알림ID 푸시알림 삭제
//     CancelNotificationAll() -> 모든 푸시알림 삭제
// *****************************************************************************************************

class modNotificator {
    companion object {
        fun showNotification(Ongoing: Boolean, ContentTitle: String, ContentText: String, id: Int = 0,
                             SmallIcon: Int = com.teamfopo.fopo.R.drawable.ic_fopo_logo,
                              LargeIcon: Int = com.teamfopo.fopo.R.drawable.img_logo) {
            val channelId = "channel"
            val channelName = "Channel Name"

            val notifManager = MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.cancel(Notification.FLAG_NO_CLEAR)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_HIGH

                val mChannel = NotificationChannel(
                    channelId, channelName, importance
                )

                notifManager.cancel(Notification.FLAG_NO_CLEAR)
                notifManager.createNotificationChannel(mChannel)
            }

            val builder = NotificationCompat.Builder(MainActivity.mContext, channelId)

            val notificationIntent = Intent(MainActivity.mContext, MainActivity::class.java)

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val requestID = System.currentTimeMillis().toInt()

            val pendingIntent = PendingIntent.getActivity(
                MainActivity.mContext,
                requestID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            builder.setContentTitle(ContentTitle) // required
                .setSmallIcon(SmallIcon)
                    //getResources().getColor(R.color.my_notif_color);
                    //#F444FF
                .setColor(MainActivity.mContext.getColor(com.teamfopo.fopo.R.color.colorPrimary))
                .setStyle(NotificationCompat.BigTextStyle().bigText(ContentText))
                .setContentText(ContentText)  // required
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(false) // 알림 터치시 반응 후 삭제
                .setOngoing(Ongoing)
                .setSound(
                    RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_ALARM)
                )

                .setLargeIcon(BitmapFactory.decodeResource(MainActivity.mContext.getResources(),  LargeIcon))

                .setContentIntent(pendingIntent)

            notifManager.notify(id, builder.build())
        }

        fun CancelNotificationAll() {
            val notifiyMgr = MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notifiyMgr!!.cancelAll()
        }

        fun CancelNotification(id: Int) {
            val notifiyMgr = MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notifiyMgr!!.cancel(id)
        }
    }
}