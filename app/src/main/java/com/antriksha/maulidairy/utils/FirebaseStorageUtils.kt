package com.antriksha.maulidairy.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.Collections


class FirebaseStorageUtils(
    private var password: String, private var listener: OnResponseListser
) {

    private val TAG = "FirebaseStorageUtils"
    private var storageRef: StorageReference = FirebaseStorage.getInstance().reference

    init {
        Log.d(TAG, "Path: ${password}")
    }

    interface OnResponseListser {
        fun onSuccess(obj: Any)
        fun onFailure(e: String)
    }

    fun listAllFiles() {
        val fileList = LinkedHashMap<String, StorageMetadata?>()
        storageRef.child("${password}").listAll().addOnSuccessListener { listResult ->
            fileList.clear()
            if (listResult.items.size == 0 && listResult.prefixes.size == 0) {
                listener.onSuccess(fileList)
            } else {
                var i = 0;
                for (item in listResult.prefixes) {
                    fileList[item.name] = null
                    listener.onSuccess(fileList)
                }
                for (item in listResult.items) {
                    //if (item.name != "obj") {
                    item.metadata.addOnSuccessListener {
                        fileList[item.name] = it
                        i++
                        if (listResult.items.size == i) {
                            fileList.toList().sortedByDescending { (key, value) ->
                                value?.updatedTimeMillis
                            }
                            fileList.remove("obj")
                            listener.onSuccess(fileList)
                        }
                    }
                    /*} else {
                        listener.onSuccess(fileList)
                    }*/
                }
            }
        }.addOnFailureListener { exception ->
            listener.onFailure(exception.message!!)
        }
    }

    fun downloadFile(storageMetadata: StorageMetadata?) {
        val child = storageRef.child("${password}").child(storageMetadata?.name.toString())
        child.getBytes(Long.MAX_VALUE).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                listener.onSuccess(it.result)
            } else {
                listener.onFailure(it.exception?.message.toString())
            }
        }
    }

    fun uploadFileToFirebase(
        selectedFileUri: Uri, displayName: String?
    ) {
        val fileRef = storageRef.child("${password}/${displayName}")
        val uploadTask = fileRef.putFile(selectedFileUri)

        uploadTask.addOnSuccessListener {
            Log.d(TAG, "uploadFileToFirebase: Upload Success")
            listener.onSuccess(it)
        }.addOnFailureListener { exception ->
            Log.d(TAG, "uploadFileToFirebase: Upload Failed: ${exception.message}")
            listener.onFailure(exception.message!!)
        }
    }

    fun createFolder(folderName: String, file: File, uri: Uri) {

        val fileRef = storageRef.child("${password}/${folderName}/${file.name}")
        val uploadTask = fileRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            Log.d(TAG, "uploadFileToFirebase: Upload Success")
            listener.onSuccess(it)
        }.addOnFailureListener { exception ->
            Log.d(TAG, "uploadFileToFirebase: Upload Failed: ${exception.message}")
        }
    }

    fun deleteFile(fileName: String) {
        val child = storageRef.child("${password}").child(fileName)
        Log.d(TAG, "deleteFile name: " + child.name)
        child.delete().addOnCompleteListener { it ->
            if (it.isSuccessful) {
                listener.onSuccess(it)
            } else {
                listener.onFailure(it.exception?.message.toString())
            }
        }
    }


}