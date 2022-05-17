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
import jcifs.context.SingletonContext
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileInputStream
import jcifs.smb.SmbFileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainViewModel: ViewModel() {
    val lineLiveData =  MutableLiveData<List<List<String>>>()
    private var linesData = ArrayList<List<String>>()

    fun takeFileContents(storageLink: String){
        viewModelScope.launch {
            getFile(storageLink)
        }
    }

    private fun getFile(storageLink: String){
        viewModelScope.launch(Dispatchers.IO) {
            val base: CIFSContext = SingletonContext.getInstance()
            val reader = csvReader{
                charset = "Windows-1251"
                delimiter = ';'
                excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
                insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
            }
            val writer = csvWriter{
                charset = "Windows-1251"
            }
            val filePath = SmbFile(storageLink, base)

            try {

                Log.i("DIR", filePath.path.toString())
                val inputSmbFileStream = SmbFileInputStream(filePath)
                val csvOutput = reader.readAll(inputSmbFileStream)
                val timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                val newFileName = timeStampPattern.format(LocalDateTime.now())
                Log.i("New file path", filePath.url.toString() + newFileName +".csv")

                val newFile = SmbFile(filePath.parent.toString() + newFileName +".csv", base)

                val outputSmbFileStream = SmbFileOutputStream(newFile)
                writer.writeAll(csvOutput, outputSmbFileStream)

                for (element in csvOutput){
                    linesData.add(element)
                }
                inputSmbFileStream.close()
            } catch (e: Exception){
                Log.e("File from LAN", e.message.toString())
            }


            viewModelScope.launch(Dispatchers.Main) {
                lineLiveData.value = linesData
            }

        }
    }
    private fun saveFile(){

    }

}