package com.lasha.csvtoobj


import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.context.ExcessFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.context.InsufficientFieldsRowBehaviour
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.hierynomus.mssmb2.SMB2CreateDisposition
import com.hierynomus.mssmb2.SMB2CreateOptions
import com.hierynomus.mssmb2.SMB2ShareAccess
import com.hierynomus.smbj.SMBClient
import com.hierynomus.smbj.SmbConfig
import com.hierynomus.smbj.auth.AuthenticationContext
import com.hierynomus.smbj.session.Session
import com.hierynomus.smbj.share.DiskShare
import com.hierynomus.smbj.share.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class MainViewModel: ViewModel() {
    val lineLiveData = MutableLiveData<List<List<String>>>()
    private var linesData = ArrayList<List<String>>()
    val exceptionData = MutableLiveData<String>()

    fun takeFileContents(storageLink: String) {
        viewModelScope.launch {
            getFile(storageLink)
        }
    }

    private fun getFile(storageLink: String) {
        viewModelScope.launch {

            try {
                connectSMB(storageLink)
            } catch (e: Exception) {
                Log.e("File from LAN", e.message.toString())
                exceptionData.postValue(e.message)
            }


            viewModelScope.launch(Dispatchers.Main) {
                lineLiveData.value = linesData
            }

        }
    }

    private fun connectSMB(smbRoot: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val reader = csvReader {
                charset = "Windows-1251"
                delimiter = ';'
                excessFieldsRowBehaviour = ExcessFieldsRowBehaviour.IGNORE
                insufficientFieldsRowBehaviour = InsufficientFieldsRowBehaviour.IGNORE
            }
            val writer = csvWriter {
                charset = "Windows-1251"
            }
            val server = "smb://192.168.1.9"
            val user = ""
            val password = ""
            val domain = ""
            val shareName = ""

            val clientConfig = SmbConfig.builder().withMultiProtocolNegotiate(true).build()
            val client = SMBClient(clientConfig)
            try {
                client.connect(server).use { connection ->
                    val ac =
                        AuthenticationContext(user, password.toCharArray(), domain)
                    val session: Session = connection.authenticate(ac)
                    val diskShare = session.connectShare(shareName) as DiskShare?
                    diskShare.use { share ->
                        share?.openFile(
                            smbRoot, null, null,
                            SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN,
                            Collections.singleton(SMB2CreateOptions.FILE_OPEN_FOR_BACKUP_INTENT)
                        )?.use { file ->
                            val inputStream = file.inputStream
                            val parsedCsv = reader.readAll(inputStream)
                            for (element in parsedCsv) {
                                linesData.add(element)
                            }
                            val timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                            val newFileName = timeStampPattern.format(LocalDateTime.now())
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}


