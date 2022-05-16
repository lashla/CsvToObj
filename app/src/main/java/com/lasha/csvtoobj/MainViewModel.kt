package com.lasha.csvtoobj

import android.os.Environment
import android.provider.MediaStore
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


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


                val root = Environment.getExternalStorageDirectory()
                val localDir = File(root.absolutePath + "/download")
                localDir.mkdirs()
                val localFile = File(localDir, "myData.csv")
                Log.i("LocalFileDir", localDir.toString())
//                val localFile = File("${MediaStore.MediaColumns.RELATIVE_PATH}/Documents/CsvData/", "fileName.csv")
//                localFile.createNewFile()

                val reader = csvReader{
                    charset = "Windows-1251"
                    excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
                    insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
                }

                val csvOutput = reader.readAll(inputSmbFileStream)
                val writer = csvWriter{
                    charset = "Windows-1251"
                }
                writer.writeAll(csvOutput, localFile)
                val csvLocalOutput = reader.readAll(localFile)
                for (element in csvLocalOutput){
                    linesData.add(element)
                }

                viewModelScope.launch(Dispatchers.Main) {
                    lineLiveData.value = linesData
                }
                inputSmbFileStream.close()
            }
    }

    init {
        lineLiveData
        getFile()
        Log.i("LiveDataValue", lineLiveData.value.toString())
    }

}