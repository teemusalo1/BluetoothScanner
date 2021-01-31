package com.example.bluetooth_scanner

class BtDevice(address: String, rssi: Int, isConnectable: Boolean) {
    val address: String = address
    val rssi: Int = rssi
    val isConnectable: Boolean = isConnectable
}