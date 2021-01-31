package com.example.bluetooth_scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

import android.os.Looper

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.annotation.RequiresApi



class MainActivity : AppCompatActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanResults: HashMap<String, ScanResult>? = null
    private var mHandler: Handler? = null
    private var mScanning: Boolean? = null
    private var mScanCallback: BtleScanCallback? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private lateinit var myListAdapter: MyAdapter

    companion object {
        const val SCAN_PERIOD: Long = 3000
    }
    lateinit var listview: ListView
    var list: ArrayList<BtDevice> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        var btn = findViewById<Button>(R.id.button)
        listview = findViewById<ListView>(R.id.list)
        myListAdapter = MyAdapter(this, list)
        btn.setOnClickListener {
        if (hasPermissions()){
            startScan()


        }}


    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startScan() {
        Log.d("DBG", "Scan start")
        mScanResults = HashMap()
        mScanCallback = BtleScanCallback()
        mBluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner
        val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .build()
        val filter: List<ScanFilter>? = null
// Stops scanning after a pre-defined scan period.
        mHandler = Handler()
        mHandler!!.postDelayed({ mBluetoothLeScanner!!.stopScan(mScanCallback) }, SCAN_PERIOD)
        mScanning = true
        mBluetoothLeScanner!!.startScan(filter, settings, mScanCallback)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class BtleScanCallback : ScanCallback() {


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            addScanResult(result)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBatchScanResults(results: List<ScanResult>) {

            for (result in results) {
                addScanResult(result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.d("DBG", "BLE Scan Failed with code $errorCode")
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun addScanResult(result: ScanResult) {

            val device = result.device
            val deviceAddress = device.address
            val deviceName = result.device.name
            val rssi = result.rssi
            if(!list.any{BtDevice -> BtDevice.address == deviceAddress}){
                list.add(BtDevice(deviceAddress, rssi, result.isConnectable))
                myListAdapter.notifyDataSetChanged()
                listview.adapter = myListAdapter
            }

            Log.d("DBG", "Device address: $deviceAddress (${result.isConnectable})")
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasPermissions(): Boolean {
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {

            Log.d("DBG", "No Bluetooth LE capability")
            return false
        } else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("DBG", "No fine location access")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
            return true // assuming that the user grants permission

        }
        return true

    }
}