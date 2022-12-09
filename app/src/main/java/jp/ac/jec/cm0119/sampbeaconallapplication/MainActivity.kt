package jp.ac.jec.cm0119.sampbeaconallapplication

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import org.altbeacon.beacon.*
import org.w3c.dom.Text

class MainActivity : AppCompatActivity(), MonitorNotifier, RangeNotifier {

    private lateinit var beaconManager: BeaconManager
    var alertDialog: AlertDialog? = null

    //permission許可の要求
    private val permissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
            //permission(権限名), isGrant(有効 or 無効)
            checkPermission(result)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()

        // TODO: applicationContent →　this RUN and MonitorNotifier, RangeNotifierをActivity実装　Activityでbeaconのセットアップ
        beaconManager = BeaconManager.getInstanceForApplication(applicationContext)
        beaconSetup()

        findViewById<Button>(R.id.start).setOnClickListener {
            // TODO: ここから 追ってみる、バックグラウンドだとどうなるのか
            beaconManager.startMonitoring(BeaconApplication.mRegion)
            beaconManager.startRangingBeacons(BeaconApplication.mRegion)
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){ //今の書き方は10は入らない
            if (checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("権限の許可")
                builder.setMessage("ビーコン機能を正常に動作させるため、設定画面より常に許可状態にしてください")
                builder.setPositiveButton(android.R.string.ok, null)
                alertDialog?.dismiss()
                alertDialog = builder.create()
                alertDialog?.show()
            }
        }

    }

    //リージョンの問題？
    override fun didEnterRegion(region: Region?) {
        Log.d("BeaconService", "リージョン内ビーコン有り")
//        var monitorTxt = findViewById<TextView>(R.id.monitor)
//        monitorTxt.text = "ビーコン領域内"
    }

    override fun didExitRegion(region: Region?) {
        Log.d("BeaconService", "リージョン内ビーコン無")
        var rangeTxt = findViewById<TextView>(R.id.range)
    }

    override fun didDetermineStateForRegion(state: Int, region: Region?) {
    }

    //monitorの監視外になっても呼び出されはするが、beaconの検知がされない
    //呼ばれてはいるが、ビーコンを検知してくれない
    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        Log.d("BeaconService",  "range")
        beacons?.forEach { beacon ->
            var rangeTxt = findViewById<TextView>(R.id.range)
            rangeTxt.text = "距離${beacon.distance}"
            Log.d("BeaconService",  "距離${beacon.distance}")
        }
    }

    private fun beaconSetup() {
        val channelId = "0"
        if(!beaconManager.isAnyConsumerBound) {
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Beacon検知中")
                .setContentText("領域監視を実行しています")

            //PendingIntentを作成
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            builder.setContentIntent(pendingIntent) //通知のクリック時の遷移


            beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(Constants.IBEACON_FORMAT)) // iBeaconのフォーマット指定
            beaconManager.foregroundBetweenScanPeriod = 5000
            beaconManager.backgroundBetweenScanPeriod = 5000
            beaconManager.backgroundScanPeriod = 1100

            beaconManager.addMonitorNotifier(this)
            beaconManager.addRangeNotifier(this)

            beaconManager.enableForegroundServiceScanning(builder.build(), 456)
            beaconManager.setEnableScheduledScanJobs(false)
        }
    }
    private fun checkPermission(result: Map<String, Boolean>) {
        //permission(権限名), isGrant(有効 or 無効)
        result.forEach { (permission, isGrant) ->
            val perm = when (permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> "位置情報の権限"
                Manifest.permission.ACCESS_COARSE_LOCATION -> "位置情報の権限"
                Manifest.permission.BLUETOOTH_SCAN -> "bluetoothの検出権限"
                Manifest.permission.BLUETOOTH_CONNECT -> "bluetoothの権限"
                else -> "その他の権限"
            }
            if (isGrant) {
                Toast.makeText(this@MainActivity, "${perm}が許可されました。", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "権限の許可を行なってください", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {    //SDKバージョンが31以下の場合
            permissionResult.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            permissionResult.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        }
    }
}
