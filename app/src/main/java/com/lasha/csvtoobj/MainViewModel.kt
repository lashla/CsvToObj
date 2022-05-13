package com.lasha.csvtoobj

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<ArrayList<String>>()

    private fun getFile(){
        val linesData = ArrayList<String>()
            viewModelScope.launch(Dispatchers.IO) {
                val url = URL("smb://192.168.1.9/shared/")
                val dir = SmbFile(url)
                Log.i("File", dir.listFiles().size.toString())
                linesData.add(dir.listFiles().toString())
            }

            lineLiveData.value = linesData

    }
    init {
        lineLiveData
        getFile()
    }

}