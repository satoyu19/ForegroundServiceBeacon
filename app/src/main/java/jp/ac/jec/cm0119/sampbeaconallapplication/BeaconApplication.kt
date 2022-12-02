package jp.ac.jec.cm0119.sampbeaconallapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import org.altbeacon.beacon.*

class BeaconApplication: Application() {    //, MonitorNotifier, RangeNotifier

        companion object {
            var insideRegion = false
            private const val BEACON_UUID_HOME = "20000124-0124-1478-1111-003033637761"
            private const val BEACON_UUID_SCHOOL = "20011478-2000-0124-1111-003033637761"
            const val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"

            val mRegion by lazy {
                //uuidの例外処理
                val uuid = try {
                    Identifier.parse("20011478-2000-0124-1111-003033637761")
                } catch (e: Exception) {
                    null
                }
                Region("beacon", uuid, null, null)
            }
        }

        override fun onCreate() {
            super.onCreate()
            val beaconManager = BeaconManager.getInstanceForApplication(this)

            if (!beaconManager.isAnyConsumerBound) {    //フォアグラウンドでの検知が開始済みだった場合にアプリが落ちるのを防ぐ
                //(フォアグラウンドで処理を行っていることをユーザーに認識させる)通知を作成
                val channelId = "0"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(
                        channelId,
                        "Beacon service",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                }
            }
        }
}