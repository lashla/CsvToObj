package com.lasha.csvtoobj

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.InetAddress
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val newFileLines = ArrayList<List<String>>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.lineLiveData.observe(this){
            if (it.isNotEmpty()) {
                Log.i("SMTH", it.toString())
                showObjectTv.text = it.toString()

            }
        }
    }



}