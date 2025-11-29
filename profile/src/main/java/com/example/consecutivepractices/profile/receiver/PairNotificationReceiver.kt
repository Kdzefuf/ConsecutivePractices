package com.example.consecutivepractices.profile.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.consecutivepractices.core.R
import com.example.consecutivepractices.data.preferences.ProfilePreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class PairNotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var profilePreferences: ProfilePreferences

    override fun onReceive(context: Context, intent: Intent) {
        val profile = runBlocking {
            profilePreferences.loadProfile()
        }

        if (profile.fullName.isNotEmpty()) {
            showNotification(context, profile.fullName)
        } else {
            showNotification(context, "Студент")
        }
    }

    private fun showNotification(context: Context, userName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "favorite_pair_channel",
                "Уведомления о парах",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о начале любимой пары"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Используем неявный Intent с действием, которое обрабатывается в манифесте
        val intent = Intent().apply {
            action = "com.example.consecutivepractices.OPEN_MAIN_ACTIVITY"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "favorite_pair_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Пора на пару!")
            .setContentText("$userName, начинается ваша любимая пара по мобильной разработке!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000))
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}