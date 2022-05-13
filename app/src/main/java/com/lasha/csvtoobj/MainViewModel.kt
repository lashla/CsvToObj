package com.lasha.csvtoobj

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jcifs.CIFSContext
import jcifs.context.SingletonContext
import jcifs.smb.NtlmPasswordAuthentication
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<ArrayList<String>>()

    private fun getFile(){
        val linesData = ArrayList<String>()
            viewModelScope.launch(Dispatchers.IO) {
                val base: CIFSContext = SingletonContext.getInstance()
                val authed1 = base.withCredentials(
                    NtlmPasswordAuthentication(
                        base, "Alexey",
                        "Lashla", "12345678"
                    )
                )

                val dir = SmbFile("smb://192.168.1.9/shared/", authed1)
                Log.i("DIR", dir.path.toString())
                linesData.add(dir.listFiles().toString())
            }

            lineLiveData.value = linesData

    }
    init {
        lineLiveData
        getFile()
    }

}