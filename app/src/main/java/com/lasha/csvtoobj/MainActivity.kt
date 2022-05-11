package com.lasha.csvtoobj

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.opencsv.CSVReader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.net.URI


class MainActivity : AppCompatActivity() {

    private var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupOnClickListeners()

    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == PICK_CSV_FILE && resultCode == Activity.RESULT_OK) {
            resultData?.let { intent ->
                Log.i("Intent data", "data - ${intent.data!!}")
                val filepath = resultData.data!!.path
                showObjectTv.text = readCsv(intent.data!!,
                    Environment.getExternalStorageDirectory().toString() +
                            filepath!!.substringAfter("/external_files"))
                    .joinToString(separator = "\n")
            }
        }
    }

    private fun setupOnClickListeners(){
        chooseScvFileBtn.setOnClickListener {
            openFile()
        }
    }



    private fun openFile(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        startActivityForResult(intent, PICK_CSV_FILE)
    }

    companion object{
        const val PICK_CSV_FILE = 123
    }




    private fun readCsv(fileName: Uri, filePath: String): ArrayList<String> {




        val allLines = ArrayList<String>()
        try {
            Log.i("FilePath", "$filePath/${getFileName(fileName)}")
            val csvFile = File(URI(filePath + "/" + getFileName(fileName)))
            Log.i("File size", "size: ${csvFile.length()}")
            val reader = CSVReader(FileReader(csvFile))

            var nextLine: Array<String>
            var element = 0
            while (reader.readNext().also { nextLine = it } != null) {
                allLines.add(nextLine[0])
                element++
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show()
        }
        return allLines
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }

}