package com.lasha.csvtoobj

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import jcifs.CIFSContext
import jcifs.context.SingletonContext
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URL
import java.nio.file.Paths


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<List<List<String>>>()

    private fun getFile(){
        var linesData = emptyList<List<String>>()
            viewModelScope.launch(Dispatchers.IO) {
//                val base: CIFSContext = SingletonContext.getInstance()

                val dir = SmbFile("smb://192.168.1.9/shared/")
                Log.i("DIR", dir.path.toString())
                Log.i("Directory files", dir.listFiles()[0].name)
                val url = URL("smb://192.168.1.9/shared/roman.csv")
                val file: String? = url.file
                linesData = csvReader().readAll(file!!)
                Log.i("Lines", "${linesData[0]}")
            }
        lineLiveData.value = linesData


    }
    init {
        lineLiveData
        getFile()
    }

}