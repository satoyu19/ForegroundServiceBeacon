package jp.ac.jec.cm0119.sampbeaconallapplication

object Constants{
        // TODO: 家様のビーコンUUID要確認
        const val TAG = "MainActivity"
        const val BEACON_UUID_HOME = "20000124-0124-1478-1111-003033637761"
        const val BEACON_UUID_SCHOOL = "20011478-2000-0124-1111-003033637761"
        const val IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
}

/**
 * テストの結果
 * BeaconManagerをActivityのapplicationContextで取得し、ボタンクリックをトリガーとして監視スタート：OK
 * BeaconManagerをActivityのthisで取得し、ボタンクリックをトリガーにスタート：OK
 * MonitorNotifier, RangeNotifierをActivityで実装し、activityでBeaconManagerにadd：OK
 *
 */