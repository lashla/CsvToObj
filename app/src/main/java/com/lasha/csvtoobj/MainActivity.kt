package com.lasha.csvtoobj

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
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
        requestPeers()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

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
    private fun requestPeers(){
        WifiP2pManager.PeerListListener {

            Log.i("AAA", it.deviceList.toString())
        }
    }

}