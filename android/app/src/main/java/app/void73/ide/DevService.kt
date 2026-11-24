package app.void73.ide

import android.app.*
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat

/** خدمة دائمة تحمي الخوادم المحلية وعمليات البناء من إنهاء النظام. */
class DevService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel("dev", "Dev environment", NotificationManager.IMPORTANCE_LOW)
        )
        val n = NotificationCompat.Builder(this, "dev")
            .setContentTitle("73 IDE")
            .setContentText("Dev services running")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .build()
        startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        return START_STICKY
    }
}
