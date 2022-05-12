package com.lasha.csvtoobj

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupOnClickListeners()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }
//    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
//        super.onActivityResult(requestCode, resultCode, resultData)
////        if (requestCode == PICK_CSV_FILE && resultCode == Activity.RESULT_OK) {
////            resultData?.let { intent ->
////                Log.i("Intent data", "data - ${intent.data!!}")
////                val filepath = resultData.data!!.path
////                showObjectTv.text = readCsv(intent.data!!,
////                    Environment.getExternalStorageDirectory().toString() +
////                            filepath!!.substringAfter("/external_files"))
////                    .joinToString(separator = "\n")
////            }
////        }
//
////        when (val result = tryHandleOpenDocumentResult(requestCode, resultCode, resultData)) {
////            OpenFileResult.DifferentResult, OpenFileResult.OpenFileWasCancelled -> { }
////            OpenFileResult.ErrorOpeningFile -> Log.e("File opening:", "error opening file")
////            is OpenFileResult.FileWasOpened -> {
////                Log.i("File", "Opened")
////
////                val file = File(Environment.getDownloadCacheDirectory().toString() + "/" + "roman.csv")
////                val rows: List<List<String>> = csvReader().readAll(file)
////                Log.i("File", "$file, rows - $rows, file size - ${file.length()}")
////                showObjectTv.text = rows.toString()
////            }
////        }
//    }




    private fun setupOnClickListeners(){
        chooseScvFileBtn.setOnClickListener {
            openFile()
        }
    }

    private val READ_STORAGE_PERMISSION_REQUEST_CODE = 41

    private fun checkPermissionForReadExterntalStorage(): Boolean {
        val result: Int = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionForReadExterntalStorage() {
        try {
            ActivityCompat.requestPermissions(
                (this as Activity?)!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_STORAGE_PERMISSION_REQUEST_CODE
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw e
        }
    }


    private fun openFile() {
        if (checkPermissionForReadExterntalStorage()) {
            viewModel.lineLiveData.observe(this){
                Log.i("SMTH", it.toString())
            }
        }
        else {
            requestPermissionForReadExterntalStorage()
        }
    }

    companion object{
        const val PICK_CSV_FILE = 2
    }

//    private fun readCsv(fileName: Uri, filePath: String): ArrayList<String> {
//
//        val allLines = ArrayList<String>()
//        try {
//            Log.i("FilePath", "$filePath/${getFileName(fileName)}")
//            val csvFile = File(URI(filePath + "/" + getFileName(fileName)))
//            Log.i("File size", "size: ${csvFile.length()}")
//            val reader = CSVReader(FileReader(csvFile))
//
//            var nextLine: Array<String>
//            var element = 0
//            while (reader.readNext().also { nextLine = it } != null) {
//                allLines.add(nextLine[0])
//                element++
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show()
//        }
//        return allLines
//    }
//
//    @SuppressLint("Range")
//    fun getFileName(uri: Uri): String? {
//        var result: String? = null
//        if (uri.scheme.equals("content")) {
//            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                }
//            } finally {
//                cursor?.close()
//            }
//        }
//        if (result == null) {
//            result = uri.path
//            val cut = result?.lastIndexOf('/')
//            if (cut != -1) {
//                if (cut != null) {
//                    result = result?.substring(cut + 1)
//                }
//            }
//        }
//        return result
//    }
//
//    fun tryHandleOpenDocumentResult(requestCode: Int, resultCode: Int, data: Intent?): OpenFileResult {
//        return if (requestCode == PICK_CSV_FILE) {
//            handleOpenDocumentResult(resultCode, data)
//        } else OpenFileResult.DifferentResult
//    }
//
//    private fun handleOpenDocumentResult(resultCode: Int, data: Intent?): OpenFileResult {
//        return if (resultCode == Activity.RESULT_OK && data != null) {
//            val contentUri = data.data
//            if (contentUri != null) {
//                val stream =
//                    try {
//                        this.application.contentResolver.openInputStream(contentUri)
//                    } catch (exception: FileNotFoundException) {
//                        return OpenFileResult.ErrorOpeningFile
//                    }
//
//                val fileName = "not implemented" // will implement file names later
//
//                if (stream != null && fileName != null) {
//                    Log.i("File", "${OpenFileResult.FileWasOpened(fileName, stream)}")
//                    OpenFileResult.FileWasOpened(fileName, stream)
//                } else OpenFileResult.ErrorOpeningFile
//            } else {
//                OpenFileResult.ErrorOpeningFile
//            }
//        } else {
//            OpenFileResult.OpenFileWasCancelled
//        }
//    }
//
//    sealed class OpenFileResult {
//        object OpenFileWasCancelled : OpenFileResult()
//        data class FileWasOpened(val fileName: String, val content: InputStream) : OpenFileResult()
//        object ErrorOpeningFile : OpenFileResult()
//        object DifferentResult : OpenFileResult()
//    }
}