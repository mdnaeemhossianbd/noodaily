package com.example.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar

class PrayerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayerNameBn = intent.getStringExtra("prayer_name_bn") ?: "সালাত"
        val prayerNameEn = intent.getStringExtra("prayer_name_en") ?: "Prayer"
        val timeFormatted = intent.getStringExtra("prayer_time") ?: ""

        Log.d("PrayerAlarmReceiver", "Triggering Azan notification for $prayerNameBn at $timeFormatted")
        PrayerNotificationManager.sendPrayerAzanNotification(
            context = context,
            prayerNameBn = prayerNameBn,
            prayerNameEn = prayerNameEn,
            timeFormatted = timeFormatted
        )

        // Reschedule alarms for next prayers
        PrayerNotificationManager.scheduleAllPrayerAlarms(context)
    }
}

object PrayerNotificationManager {
    private const val TAG = "PrayerNotificationMgr"
    const val CHANNEL_ID = "prayer_azan_channel"
    private const val CHANNEL_NAME = "নামাজের আযান ও ওয়াক্ত নোটিফিকেশন"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        context.packageName + "/" + R.raw.azan_tone
            )

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "প্রতিটি নামাজের ওয়াক্ত শুরুর সাথে সাথে আযানের মধুর ধ্বনিসহ নোটিফিকেশন"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 250, 500)
                setSound(soundUri, audioAttributes)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun sendPrayerAzanNotification(
        context: Context,
        prayerNameBn: String,
        prayerNameEn: String,
        timeFormatted: String
    ) {
        createNotificationChannel(context)

        // Play Azan MediaPlayer directly for ~12 seconds to guarantee original Azan tone sounds
        try {
            val mediaPlayer = MediaPlayer.create(context, R.raw.azan_tone)
            mediaPlayer?.setOnCompletionListener { mp ->
                try { mp.release() } catch (_: Exception) {}
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error playing Azan tone: ${e.message}", e)
        }

        val soundUri = Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    context.packageName + "/" + R.raw.azan_tone
        )

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerNameBn.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_noor_daily_icon)
            .setContentTitle("সালাতের ওয়াক্ত শুরু হয়েছে: $prayerNameBn ($prayerNameEn)")
            .setContentText("এখন $prayerNameBn নামাজের আউয়াল ওয়াক্ত ($timeFormatted)। নামাজ আদায় করতে মসজিদে আসুন।")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    "পবিত্র $prayerNameBn সালাতের ওয়াক্ত ($timeFormatted) শুরু হয়েছে।\n" +
                            "রাসূলুল্লাহ (সা.) ইরশাদ করেছেন: \"যে ব্যক্তি যথাসময়ে নামাজ আদায় করে, আল্লাহ তার জন্য জান্নাতের প্রতিশ্রুতি দিয়েছেন।\""
                )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(prayerNameBn.hashCode(), notification)
    }

    /**
     * Test function callable from UI so user can immediately verify Azan sound and notification
     */
    fun testAzanNotification(context: Context) {
        sendPrayerAzanNotification(
            context = context,
            prayerNameBn = "টেস্ট আযান",
            prayerNameEn = "Test Azan",
            timeFormatted = "বর্তমান ওয়াক্ত"
        )
    }

    /**
     * Schedules exact alarms for all 5 daily prayers (Fajr, Dhuhr, Asr, Maghrib, Isha)
     */
    fun scheduleAllPrayerAlarms(context: Context, location: WorldLocation = IslamicTimeManager.DEFAULT_LOCATION) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prayerTimes = IslamicTimeManager.calculatePrayerTimes(location)

        val cal = Calendar.getInstance()
        val nowMillis = System.currentTimeMillis()

        // 5 main obligatory prayer waqts
        val waqts = listOf(
            Triple("ফজর", "Fajr", prayerTimes.fajr),
            Triple("যোহর", "Dhuhr", prayerTimes.dhuhr),
            Triple("আসর", "Asr", prayerTimes.asr),
            Triple("মাগরিব", "Maghrib", prayerTimes.maghrib),
            Triple("ইশা", "Isha", prayerTimes.isha)
        )

        waqts.forEachIndexed { index, (nameBn, nameEn, timeStr) ->
            val targetCal = parseTimeStringToCalendar(timeStr, location.timezoneId)
            // If already passed today, schedule for tomorrow
            if (targetCal.timeInMillis <= nowMillis) {
                targetCal.add(Calendar.DAY_OF_YEAR, 1)
            }

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra("prayer_name_bn", nameBn)
                putExtra("prayer_name_en", nameEn)
                putExtra("prayer_time", timeStr)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1000 + index,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetCal.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        targetCal.timeInMillis,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Scheduled Azan alarm for $nameBn at ${targetCal.time}")
            } catch (e: Exception) {
                Log.w(TAG, "Could not set exact alarm for $nameBn: ${e.message}")
            }
        }
    }

    private fun parseTimeStringToCalendar(timeStr: String, timezoneId: String): Calendar {
        val cal = IslamicTimeManager.getCalendarForTimezone(timezoneId)
        try {
            // Converts Bengali digits to English digits: e.g. "০৫:১২ AM" -> "05:12 AM"
            val enTime = timeStr.map { ch ->
                when (ch) {
                    '০' -> '0'
                    '১' -> '1'
                    '২' -> '2'
                    '৩' -> '3'
                    '৪' -> '4'
                    '৫' -> '5'
                    '৬' -> '6'
                    '৭' -> '7'
                    '৮' -> '8'
                    '৯' -> '9'
                    else -> ch
                }
            }.joinToString("").trim()

            val parts = enTime.split(" ")
            val timeParts = parts[0].split(":")
            var hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val isPm = parts.getOrNull(1)?.equals("PM", ignoreCase = true) == true

            if (isPm && hour < 12) hour += 12
            if (!isPm && hour == 12) hour = 0

            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing time string '$timeStr': ${e.message}")
        }
        return cal
    }
}
