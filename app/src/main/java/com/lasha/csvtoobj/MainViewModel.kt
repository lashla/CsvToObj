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
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
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
    private var linesData = ArrayList<List<String>>()
    private val CSV_HEADER = "date,name,number,some"

    private fun takeFileContents(){
        viewModelScope.launch {
            getFile()
        }
    }

    private fun getFile(){
            viewModelScope.launch(Dispatchers.IO) {
                val base: CIFSContext = SingletonContext.getInstance()
                val dir = SmbFile("smb://192.168.1.9/shared/", base)
                Log.i("DIR", dir.path.toString())
                Log.i("Directory files", dir.listFiles()[0].toString())
                val inputSmbFileStream = SmbFileInputStream(dir.listFiles()[0])

                val localFile = File.createTempFile("fileName", ".csv")

                val outputFileStream = FileOutputStream(localFile)
                val reader = csvReader{
                    charset = "UTF-8"
                    excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
                    insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
                }
                val csvOutput = reader.readAll(inputSmbFileStream)
                for (element in csvOutput){
                    linesData.add(element)
                }
                viewModelScope.launch(Dispatchers.Main) {
                    lineLiveData.value = linesData
                }
                inputSmbFileStream.close()
                outputFileStream.close()
            }
    }

    init {
        lineLiveData
        getFile()
        Log.i("LiveDataValue", lineLiveData.value.toString())
    }

}