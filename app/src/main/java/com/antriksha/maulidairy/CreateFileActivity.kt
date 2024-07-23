package com.antriksha.maulidairy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.antriksha.maulidairy.utils.EncryptionCallback
import com.antriksha.maulidairy.utils.FileEncryptionUtility
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException


class CreateFileActivity : BaseActivity() {

    private val TAG = "CreateFileActivity"
    var mEditText: EditText? = null
    var et_filename: EditText? = null
    var delete: Button? = null
    private lateinit var storageRef: StorageReference
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_file)
        storageRef = FirebaseStorage.getInstance().reference
        mEditText = findViewById<EditText>(R.id.et_content)
        et_filename = findViewById<EditText>(R.id.et_filename)
        delete = findViewById<Button>(R.id.delete)
        val fileName = intent.getStringExtra("fileName")
        val filePath = intent.getStringExtra("filePath")
        path = intent.getStringExtra("path").toString()
        if(!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(fileName)) {
            val file = File(filePath)
            if (file.exists()) {
                et_filename?.setText(fileName)

                val reader = FileReader(filePath)
                val txt = reader.readText()
                mEditText?.setText(txt)
                reader.close()

                findViewById<Button>(R.id.upload).setText("Update File")
                delete?.visibility = View.VISIBLE
                delete?.setOnClickListener {
                    if (fileName != null) {
                        showLoader(mContext, "Deleting..")
                        storageRef.child("${path}").child("$fileName.txt").delete()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    dismissLoader()
                                    showSuccessToast("File deleted successfully")
                                    onBackPressed()
                                } else {
                                    dismissLoader()
                                    showErrorToast("failed to delete file")
                                }
                            }
                    }
                }
            } else {
                delete?.visibility = View.GONE
            }
        }
    }

    fun save(v: View?) {
        val text = mEditText!!.text.toString().trim()
        val etFilename = et_filename!!.text.toString().trim()
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(etFilename)) {
            FancyToast.makeText(
                this, "Please enter file name and file content",
                FancyToast.LENGTH_LONG,
                FancyToast.ERROR, true
            ).show()
            return
        }
        var fos: FileOutputStream? = null
        val file = File(getExternalFilesDir(""), "$etFilename.txt")
        try {
            fos = FileOutputStream(file)
            fos.write(text.toByteArray())
            val outputFile = File(filesDir, "$etFilename.txt")
            FileEncryptionUtility(BuildConfig.ENC_KEY).encryptFile(file, outputFile, object: EncryptionCallback {
                override fun onSuccess(message: String) {
                    val uri = Uri.fromFile(outputFile)
                    uploadFileToFirebase(uri)
                }

                override fun onFailure(error: String) {

                }
            })

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun uploadFileToFirebase(selectedFileUri: Uri) {
        showLoader(this@CreateFileActivity, "Uploading file...")
        val fileRef = storageRef.child("${path}/${selectedFileUri.lastPathSegment}")
        val uploadTask = fileRef.putFile(selectedFileUri)

        uploadTask.addOnSuccessListener {
            dismissLoader()
            Log.d(TAG, "uploadFileToFirebase: Upload Success")
            showSuccessToast("Upload Success")
            finish()
        }.addOnFailureListener { exception ->
            dismissLoader()
            Log.d(TAG, "uploadFileToFirebase: Upload Failed: ${exception.message}")
            showErrorToast("Upload failed")
        }
    }

    companion object {
        fun openActivity(mainActivity: Context, path: String) {
            val intent = Intent(mainActivity, CreateFileActivity::class.java)
            intent.putExtra("path", path)
            mainActivity.startActivity(intent)
        }

        fun openActivity(mainActivity: Context, fileName: String, filePath: String, path: String) {
            val intent = Intent(mainActivity, CreateFileActivity::class.java)
            intent.putExtra("fileName", fileName)
            intent.putExtra("filePath", filePath)
            intent.putExtra("path", path)
            mainActivity.startActivity(intent)
        }
    }
}