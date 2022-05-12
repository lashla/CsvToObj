package com.lasha.csvtoobj

import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<ArrayList<String>>()
    val devices = MutableLiveData<ArrayList<String>>()

    private fun getFile(){
        val linesData = ArrayList<String>()
        viewModelScope.launch(Dispatchers.IO){
            var line: String?
            val url = URL("smb://192.168.1.9/shared/roman.csv")
            val connection = url.openConnection()
            BufferedReader(InputStreamReader(connection.getInputStream())).use{ inp ->
                while (inp.readText().also { line = it } != null) {
                    linesData.add(line!!)
                }
            }
            lineLiveData.value = linesData
        }
    }
    init {
        lineLiveData
        getFile()
    }

}