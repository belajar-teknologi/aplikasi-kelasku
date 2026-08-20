package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    private const val CHANNEL_ID_EXAM = "channel_guru_exam"
    private const val CHANNEL_ID_HOMEWORK = "channel_guru_homework"
    private const val CHANNEL_ID_GENERAL = "channel_guru_general"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val examChannel = NotificationChannel(
                CHANNEL_ID_EXAM,
                "Jadwal Ujian & PTS",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pengingat pelaksanaan ujian dan penilaian semester"
            }

            val homeworkChannel = NotificationChannel(
                CHANNEL_ID_HOMEWORK,
                "Tenggat Pengumpulan Tugas (PR)",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi tenggat waktu pengumpulan PR dan tugas kelas"
            }

            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                "Informasi Guru & Absensi",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifikasi absensi dan laporan nilai otomatis"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(examChannel)
            manager.createNotificationChannel(homeworkChannel)
            manager.createNotificationChannel(generalChannel)
        }
    }

    fun showPushNotification(
        context: Context,
        title: String,
        message: String,
        type: String = "GENERAL"
    ) {
        val channelId = when (type) {
            "UJIAN" -> CHANNEL_ID_EXAM
            "TUGAS" -> CHANNEL_ID_HOMEWORK
            else -> CHANNEL_ID_GENERAL
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        } catch (e: SecurityException) {
            // Permission not granted yet
        }
    }
}
