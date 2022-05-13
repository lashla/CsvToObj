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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.InetAddress
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupOnClickListeners()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        initView()
    }

    private fun setupOnClickListeners(){
        chooseScvFileBtn.setOnClickListener {

        }
    }

    private fun initView() {
        viewModel.lineLiveData.observe(this){
            Log.i("SMTH", it.toString())
            showObjectTv.text = it.toString()
        }
    }

}