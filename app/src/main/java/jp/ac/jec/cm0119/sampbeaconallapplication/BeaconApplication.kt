package jp.ac.jec.cm0119.sampbeaconallapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import jp.ac.jec.cm0119.sampbeaconallapplication.Constants.BEACON_UUID_HOME
import jp.ac.jec.cm0119.sampbeaconallapplication.Constants.BEACON_UUID_SCHOOL
import org.altbeacon.beacon.*

class BeaconApplication: Application() {    //, MonitorNotifier, RangeNotifier

        companion object {
            var insideRegion = false

            val mRegion by lazy {
                //uuidの例外処理
                val uuid = try {
                    Identifier.parse(BEACON_UUID_SCHOOL)
                } catch (e: Exception) {
                    null
                }
                Region("schoolPhone", uuid, null, null)
            }
        }

        override fun onCreate() {
            super.onCreate()
            val beaconManager = BeaconManager.getInstanceForApplication(this)

            if (!beaconManager.isAnyConsumerBound) {    //フォアグラウンドでの検知が開始済みだった場合にアプリが落ちるのを防ぐ
                //(フォアグラウンドで処理を行っていることをユーザーに認識させる)通知を作成
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channelId = "0"
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