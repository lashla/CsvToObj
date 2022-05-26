package com.lasha.csvtoobj


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<List<List<String>>>()
    private var linesData = ArrayList<List<String>>()
    val exceptionData = MutableLiveData<String>()
    private val reader = csvReader{
        charset = "Windows-1251"
        excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
        insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
        escapeChar ='\\'
    }
    private val writer = csvWriter{
        charset = "Windows-1251"
    }

    fun takeFileContents(storageLink: String){
        viewModelScope.launch {
            getFileFromLink(storageLink)
        }

    }

    fun getFileFromStorage(file: File){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val fileInputStream = FileInputStream(file)
                reader.readAll(fileInputStream).forEach {
                    linesData.add(it)
                    Log.i("LinesData", it.toString())
                }

                fileInputStream.close()
            } catch (e: Exception){
                exceptionData.postValue(e.message)
                Log.e("Read File From Storage", e.message.toString())
            }
            viewModelScope.launch(Dispatchers.Main){
                lineLiveData.value = linesData
            }
        }

    }

    private fun getFileFromLink(storageLink: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val smb = connectSMB(storageLink)
                Log.i("DIR", smb.path.toString())

                    val inputSmbFileStream = SmbFileInputStream(smb)
                    reader.readAll(inputSmbFileStream).forEach {
                        linesData.add(it)
                    }
//                val csvOutput = reader.readAll(inputSmbFileStream)
                    val timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                    val newFileName = timeStampPattern.format(LocalDateTime.now())
                    Log.i("New file path", smb.url.toString() + newFileName +".csv")

                    val newFile = SmbFile(smb.parent.toString() + newFileName +".csv")

                    val outputSmbFileStream = SmbFileOutputStream(newFile)
                    writer.writeAll(linesData, outputSmbFileStream)

//                    for (element in csvOutput){
//                        linesData.add(element)
//                    }
                    inputSmbFileStream.close()
                    outputSmbFileStream.close()

            } catch (e: Exception){
                Log.e("File from LAN", e.message.toString())
                exceptionData.postValue(e.message)
            }


            viewModelScope.launch(Dispatchers.Main) {
                lineLiveData.value = linesData
            }

        }
    }

    private suspend fun connectSMB(smbRoot: String): SmbFile {
        val smb: SmbFile = withContext(Dispatchers.IO) {
                val prop = Properties()
                prop.setProperty("jcifs.smb.client.minVersion", "SMB202")
                prop.setProperty("jcifs.smb.client.maxVersion", "SMB300")
                val bc = BaseContext(PropertyConfiguration(prop))
                val creds = NtlmPasswordAuthentication(bc.withAnonymousCredentials())
                val auth: CIFSContext = bc.withCredentials(creds)
                SmbFile(smbRoot, auth)
            }
        return smb
    }

}