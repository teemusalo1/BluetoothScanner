package com.example.bluetooth_scanner

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.ArrayList

class MyAdapter(private val context: Activity, private val title: ArrayList<BtDevice>): ArrayAdapter<BtDevice>(context, R.layout.custom_list, title) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)
        val text = rowView.findViewById(R.id.textView) as TextView
        if (title[position].isConnectable){
            text.setTextColor(Color.parseColor("#0aad3f"))
        } else {
            text.setTextColor(Color.parseColor("#7e848b"))
        }
        text.text = "${title[position].address} RSSI: ${title[position].rssi} dBm"
        return rowView
    }
}

