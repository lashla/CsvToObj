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
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.InetAddress
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val adapter = ScvRecyclerViewAdapter()
    private val data = ArrayList<CsvData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enterRoot()
        initViewModel()
        initRecyclerView()

    }

    private fun enterRoot(){
        searchPath.setOnClickListener {
            viewModel.takeFileContents("smb://"+ filePathEt.text.toString() + ".csv")
            exceptionTV.visibility = View.INVISIBLE
        }
    }



    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.lineLiveData.observe(this){
            if (it.isNotEmpty()) {
                Log.i("viewmodel", "SMTH")
                for (element in it)
                    {
                        recyclerView.visibility = View.VISIBLE
                       data.add(CsvData(element[0],element[1],element[2],element[3],element[4],element[5],element[6]))
                       adapter.updateDbInfo(data)
                    }
                }  else {
                    viewModel.exceptionData.observe(this){ exception ->
                        if (exception.isNotEmpty()){
                            recyclerView.visibility = View.INVISIBLE

                            exceptionTV.text = exception
                            exceptionTV.visibility = View.VISIBLE
                        }

                    }
            }
            }
        }


    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

}