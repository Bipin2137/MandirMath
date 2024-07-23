package com.antriksha.maulidairy.utils

import android.os.AsyncTask
import java.io.*
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec

class FileEncryptionUtility(private val key: String) {

    private val algorithm = "AES"

    fun encryptFile(inputFile: String, outputFile: File, callback: EncryptionCallback) {
        EncryptAsyncTask(File(inputFile), outputFile, callback).execute()
    }

    fun decryptFile(inputFile: String, outputFile: File, callback: DecryptionCallback) {
        DecryptAsyncTask(File(inputFile), outputFile, callback).execute()
    }


    fun encryptFile(inputFile: File, outputFile: File, callback: EncryptionCallback) {
        EncryptAsyncTask(inputFile, outputFile, callback).execute()
    }

    fun decryptFile(inputFile: File, outputFile: File, callback: DecryptionCallback) {
        DecryptAsyncTask(inputFile, outputFile, callback).execute()
    }

    private inner class EncryptAsyncTask(
        private val inputFile: File,
        private val outputFile: File,
        private val callback: EncryptionCallback
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean? {
            try {
                val keySpec: Key = SecretKeySpec(key.toByteArray(), algorithm)
                val cipher = Cipher.getInstance(algorithm)
                cipher.init(Cipher.ENCRYPT_MODE, keySpec)

                val inputStream = FileInputStream(inputFile)
                val outputStream = CipherOutputStream(FileOutputStream(outputFile), cipher)

                val buffer = ByteArray(1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                return true
            } catch (e: Exception) {
                return false
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            if (result == true) {
                callback.onSuccess("File encrypted successfully")
            } else {
                callback.onFailure("Encryption failed")
            }

        }
    }

    private inner class DecryptAsyncTask(
        private val inputFile: File,
        private val outputFile: File,
        private val callback: DecryptionCallback
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean? {
            try {
                val keySpec: Key = SecretKeySpec(key.toByteArray(), algorithm)
                val cipher = Cipher.getInstance(algorithm)
                cipher.init(Cipher.DECRYPT_MODE, keySpec)

                val inputStream = CipherInputStream(FileInputStream(inputFile), cipher)
                val outputStream = FileOutputStream(outputFile)

                val buffer = ByteArray(1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                return true
            } catch (e: Exception) {
                return false
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            if (result == true) {
                callback.onSuccess("File decrypted successfully")
            } else {
                callback.onFailure("Decryption failed")
            }
        }
    }
}

interface EncryptionCallback {
    fun onSuccess(message: String)
    fun onFailure(error: String)
}

interface DecryptionCallback {
    fun onSuccess(message: String)
    fun onFailure(error: String)
}
