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
        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = WiFiDirectBroadcastReceiver(manager!!, channel, this)
        }
        var i = 0
        WifiP2pManager.PeerListListener { i = it.deviceList.size }
        Log.i("Devices", i.toString())
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

    }

    override fun onResume() {
        super.onResume()
        receiver?.also { receiver ->
            registerReceiver(receiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        receiver?.also { receiver ->
            unregisterReceiver(receiver)
        }
    }

    private fun setupOnClickListeners(){
        chooseScvFileBtn.setOnClickListener {
            openFile()
        }
    }

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41

    private fun checkPermissionForReadExterntalStorage(): Boolean {
        val result: Int = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForReadExterntalStorage() {
        try {
            ActivityCompat.requestPermissions(
                (this as Activity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }
    }


    private fun openFile() {
        if (checkPermissionForReadExterntalStorage()) {
            viewModel.lineLiveData.observe(this){
                Log.i("SMTH", it.toString())
                showObjectTv.text = it.toString()
            }
        }
        else {
            requestPermissionForReadExterntalStorage()
        }
    }

    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }



}