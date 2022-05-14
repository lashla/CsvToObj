package com.lasha.csvtoobj

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
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
import java.io.FileOutputStream
import java.io.FileWriter
import java.net.URL


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<List<List<String>>>()

    private val CSV_HEADER = "date,name,number,some"

    private fun getFile(){
        var linesData = ArrayList<List<String>>()
            viewModelScope.launch(Dispatchers.IO) {
                val base: CIFSContext = SingletonContext.getInstance()
                val dir = SmbFile("smb://192.168.1.9/shared/", base)
                Log.i("DIR", dir.path.toString())
                Log.i("Directory files", dir.listFiles()[0].toString())
                val inputSmbFileStream = SmbFileInputStream(dir.listFiles()[0])

                val localFile = File.createTempFile("fileName", ".csv")

                val outputFileStream = FileOutputStream(localFile)

                val smth = csvReader().readAll(SmbFileInputStream(dir.listFiles()[0]))
                for (element in smth){
                    linesData.add(element)
                }
                Log.i("Something", smth.toString())
                inputSmbFileStream.close()
                outputFileStream.close()

                Log.i("Lines", "${localFile.length()} ${localFile.canonicalPath}")

            }
        lineLiveData.value = linesData


    }

    init {
        lineLiveData
        getFile()
    }

}