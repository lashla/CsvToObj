package com.lasha.csvtoobj

import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import jcifs.CIFSContext
import jcifs.context.SingletonContext
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import jcifs.smb.SmbFileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<ArrayList<List<String>>>()

    private fun getFile(){

        var linesData = ArrayList<List<String>>()
            viewModelScope.launch(Dispatchers.IO) {
                val base: CIFSContext = SingletonContext.getInstance()

                val dir = SmbFile("smb://192.168.1.9/shared/")
                Log.i("DIR", dir.path.toString())
                Log.i("Directory files", dir.listFiles()[0].name)
                val file = SmbFile("smb://192.168.1.9/shared/roman.csv/")
                val smbFileInput = SmbFileInputStream(file)

//                linesData = csvReader().readAll(File) as ArrayList<List<String>>
            }

            lineLiveData.value = linesData

    }
    init {
        lineLiveData
        getFile()
    }

}