package com.lasha.csvtoobj

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.coroutines.CoroutineContext

class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<ArrayList<String>>()

    fun getFile(){
        val linesData = ArrayList<String>()
        viewModelScope.launch(Dispatchers.IO){
            var line: String?
            val url = URL("\\\\\\\\192.168.100.23\\\\share")
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