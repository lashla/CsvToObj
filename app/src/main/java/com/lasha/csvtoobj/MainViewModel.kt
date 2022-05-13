package com.lasha.csvtoobj

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<ArrayList<String>>()

    private fun getFile(){
        val linesData = ArrayList<String>()

            val url = "smb://192.168.1.9/shared/"
            val auth = NtlmPasswordAuthentication(null, "user", "password")
            val dir = SmbFile(url, auth)
                Log.i("File", dir.listFiles().toString())
                linesData.add(dir.listFiles().toString())

            lineLiveData.value = linesData

    }
    init {
        lineLiveData
        getFile()
    }

}