package com.example.mobileproject

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ReminderBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val i: Intent = Intent(context, MainActivity::class.java)
        intent!!.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context,0,i,0)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context!!,"notifyMyDialysis")
            .setSmallIcon(R.drawable.ic_baseline_local_hospital_24)
            .setContentTitle("Your booked dialysis")
            .setContentText("Hey, you have your dialysis in 30 minutes !")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

        notificationManager.notify(200,builder.build())
    }
}