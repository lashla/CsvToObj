package com.lasha.csvtoobj

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private val adapter = ScvRecyclerViewAdapter()
    private val data = ArrayList<CsvData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupOnClickListeners()
        initViewModel()
        initRecyclerView()
        checkPermissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (resultCode == RESULT_OK){
            val uri = resultData!!.data
            val pathParent = resultData.data!!.path!!.replace("${resultData.data!!.lastPathSegment}", "")
            Log.i("path parent", pathParent)
//            val filePath = uri!!.path
//            val uriToPath = contentResolver.openInputStream(uri)
//            Log.i("Uri", uri)
            val inputStream: InputStream? = contentResolver.openInputStream(uri!!)
            val file = File.createTempFile("fileName", ".csv", this.cacheDir)
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(10000)
            var bytesRead: Int

            while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }
            inputStream.close()
            outputStream.close()

            importCsv(file, pathParent)
        }
    }

    private fun setupOnClickListeners(){
        searchPath.setOnClickListener {
            viewModel.takeFileContents("smb://"+ filePathEt.text.toString() + ".csv")
            exceptionTV.visibility = View.INVISIBLE
        }
        openFileExplorerBtn.setOnClickListener {
            chooseFileFromStorage()
        }
    }

    private fun chooseFileFromStorage(){
        val PICK_CSV_FILE = 1

            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, "Open csv")
            }
            startActivityForResult(intent, PICK_CSV_FILE)
    }

    private fun importCsv(file: File, path: String){
        val reader = csvReader{
            charset = "Windows-1251"
            delimiter = ';'
            excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
            insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
        }
        val writer = csvWriter{
            charset = "Windows-1251"
        }

        val fileInputStream = FileInputStream(file)
        val csvOutput = reader.readAll(fileInputStream)
        val timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val newFileName = timeStampPattern.format(LocalDateTime.now())
        showLocalCsv(csvOutput)
//        val newFile = File("$path$newFileName.csv")
//        if (newFile.exists()){
//            val fileOutputStream = FileOutputStream(newFile)
//            writer.writeAll(csvOutput, fileOutputStream)
//            fileOutputStream.close()
//        }

        fileInputStream.close()

    }

    private fun showLocalCsv(csvOutput: List<List<String>>){
        for (element in csvOutput){
            recyclerView.visibility = View.VISIBLE
            data.add(CsvData(element[0],element[1],element[2],element[3],element[4],element[5],element[6]))
            adapter.updateDbInfo(data)
        }
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.lineLiveData.observe(this){
            if (it.isNotEmpty()) {
                Log.i("viewmodel", "SMTH")
                for (element in it)
                    {
                        recyclerView.visibility = View.VISIBLE
                        data.add(CsvData(element[0],element[1],element[2],element[3],element[4],element[5],element[6]))
                        adapter.updateDbInfo(data)
                    }
                }  else {
                    viewModel.exceptionData.observe(this){ exception ->
                        if (exception.isNotEmpty()){
                            recyclerView.visibility = View.INVISIBLE

                            exceptionTV.text = exception
                            exceptionTV.visibility = View.VISIBLE
                        }

                    }
            }
            }
        }


    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

    private fun checkPermissions(){
        if (!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSIONS
            );
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it) == PackageManager.PERMISSION_GRANTED
    }
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).apply {
            }.toTypedArray()
    }


}