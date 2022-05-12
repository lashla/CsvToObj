package com.lasha.csvtoobj

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
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
        getClientList(true, 1000)
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

    fun getClientList(
        onlyReachables: Boolean,
        reachableTimeout: Int
    ): ArrayList<ClientScanResult?>? {
        var buffReader: BufferedReader? = null
        var result: ArrayList<ClientScanResult?>? = null
        try {
            result = ArrayList<ClientScanResult?>()
            buffReader = BufferedReader(FileReader("/proc/net/arp"))
            var line: String
            var i = 0
            while (buffReader.readLine().also { line = it } != null) {
                val splitted = line.split(" +").toTypedArray()
                if (splitted.size >= 4) {
                    val mac = splitted[3]
                    if (mac.matches(Regex("..:..:..:..:..:.."))) {
                        val isReachable: Boolean =
                            InetAddress.getByName(splitted[0]).isReachable(reachableTimeout)
                        if (!onlyReachables || isReachable) {
                            result.add(
                                ClientScanResult(
                                    splitted[0],
                                    splitted[3],
                                    splitted[5],
                                    isReachable
                                )
                            )
                            Log.i(this.javaClass.toString(), result.toString())
                            i++
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), e.message!!)
        } finally {
            try {
                buffReader!!.close()
            } catch (e: IOException) {
                Log.e(this.javaClass.toString(), e.toString())
            }
        }
        return result
    }
}