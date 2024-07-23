package com.antriksha.maulidairy

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.antriksha.maulidairy.utils.QRCodeENCDEC
import com.antriksha.maulidairy.utils.SPrefs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val container_createfolder: RelativeLayout? = null
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private val TAG = "MainActivity"
    private lateinit var storageRef: StorageReference
    private lateinit var fileAdapter: FileAdapter
    private var recyclerView: RecyclerView? = null
    private var fileList: LinkedHashMap<String, StorageMetadata> = LinkedHashMap()
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        password = QRCodeENCDEC().decrypt(
            SPrefs.get(this@MainActivity, SPrefs.KEY_PASSWORD),
            BuildConfig.ENC_KEY
        )
        val container_uploadfile = findViewById<RelativeLayout>(R.id.container_uploadfile)
        val container_createfile = findViewById<RelativeLayout>(R.id.container_createfile)
        val container_createfolder = findViewById<RelativeLayout>(R.id.container_createfolder)

        container_createfile.setOnClickListener {
            CreateFileActivity.Companion.openActivity(this@MainActivity, "")
        }

        storageRef = FirebaseStorage.getInstance().reference
        fileList = LinkedHashMap()
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            openFilePickerIntent()
        }

        container_uploadfile.setOnClickListener {
            //openFilePickerIntent()
            val emptyFile = File(getExternalFilesDir(null), "obj")
            emptyFile.createNewFile()
            uploadFileToFirebase(
                "Documents",
                FileProvider.getUriForFile(
                    this@MainActivity,
                    BuildConfig.APPLICATION_ID + ".provider",
                    emptyFile
                ),
                emptyFile.name
            )
        }

        container_createfolder.setOnClickListener {

        }

        fileAdapter = FileAdapter(fileList, object : OnItemClick {
            override fun onItemClick(storageMetadata: StorageMetadata?) {
                if (storageMetadata != null) {
                    downloadFile(storageMetadata)
                }
            }
        })

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = fileAdapter
        }

        mSwipeRefreshLayout = findViewById(R.id.swipe_layout) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this)
        mSwipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_red_light,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(Runnable {
            mSwipeRefreshLayout.setRefreshing(true)

            // Fetching data from server
            loadFileList()
        })
    }

    private fun openFilePickerIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"), 1
            )
        } catch (ex: ActivityNotFoundException) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(
                this, "Please install a File Manager.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun uploadFileToFirebase(selectedFileUri: Uri, displayName: String?) {
        showLoader(this@MainActivity, "Uploading file...")
        val fileRef = storageRef.child("${password}/${displayName}")
        val uploadTask = fileRef.putFile(selectedFileUri)

        uploadTask.addOnSuccessListener {
            dismissLoader()
            Log.d(TAG, "uploadFileToFirebase: Upload Success")
            showSuccessToast("Upload Success")
            loadFileList()
        }.addOnFailureListener { exception ->
            dismissLoader()
            showErrorToast("Upload failed, try again")
            Log.d(TAG, "uploadFileToFirebase: Upload Failed: ${exception.message}")
        }
    }

    private fun uploadFileToFirebase(
        folerName: String,
        selectedFileUri: Uri,
        displayName: String?
    ) {
        showLoader(this@MainActivity, "Uploading file...")
        val fileRef = storageRef.child("${password}/${folerName}/${displayName}")
        val uploadTask = fileRef.putFile(selectedFileUri)

        uploadTask.addOnSuccessListener {
            dismissLoader()
            Log.d(TAG, "uploadFileToFirebase: Upload Success")
            showSuccessToast("Upload Success")
            loadFileList()
        }.addOnFailureListener { exception ->
            dismissLoader()
            showErrorToast("Upload failed, try again")
            Log.d(TAG, "uploadFileToFirebase: Upload Failed: ${exception.message}")
        }
    }

    private fun loadFileList() {
        mSwipeRefreshLayout.isRefreshing = true

    }

    private fun downloadFile(storageMetadata: StorageMetadata?) {
        showLoader(this@MainActivity, "Please wait...")
        val child = storageRef.child("${password}").child(storageMetadata?.name.toString())

        child.getBytes(Long.MAX_VALUE).addOnSuccessListener {
            dismissLoader()
            if (storageMetadata?.contentType?.contains("octet") == true) {
                val filename = storageMetadata.name.toString()
                    .substring(0, storageMetadata.name.toString().indexOf("."))
                val fileContent = String(it)
                //CreateFileActivity.openActivity(this@MainActivity, filename, fileContent, "")
            } else if (storageMetadata?.contentType?.contains("image") == true) {
                //val filename = storageMetadata.name.toString().substring(0, storageMetadata.name.toString().indexOf("."))
                //val fileContent = it
                //ImageViewActivity.openImage(this@MainActivity, it)
            } else {
                val file = storageMetadata?.name?.let { it1 -> File(getExternalFilesDir(""), it1) }
                if (!file?.exists()!!) {
                    file?.createNewFile()
                }
                val fos = FileOutputStream(file)
                fos.write(it)
                fos.close()
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.data = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.antariksha.maulidairy.provider",
                    file
                )
                startActivity(intent)

            }
        }
    }

    interface OnItemClick {
        fun onItemClick(storageMetadata: StorageMetadata?)
    }

    inner class FileAdapter(
        private val fileList: LinkedHashMap<String, StorageMetadata>,
        private val onItemClick: OnItemClick
    ) :
        RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

        inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
            val fileNameDate: TextView = itemView.findViewById(R.id.fileNameDate)
            val iv_icon: ImageView = itemView.findViewById(R.id.iv_icon)

            init {
                itemView.setOnClickListener {
                    onItemClick.onItemClick(getElementByIndex(fileList, adapterPosition))
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
            return FileViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
            holder.fileNameTextView.text = getElementNameByIndex(fileList, position)
            val contentType = fileList[getElementNameByIndex(fileList, position)]
            holder.fileNameDate.text = "Modified ${getModifiedDate(contentType?.updatedTimeMillis)}"
            if (!TextUtils.isEmpty(contentType?.contentType)) {
                if (contentType?.contentType?.contains("pdf") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.pdf
                        )
                    )
                } else if (contentType?.contentType?.contains("image") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.img
                        )
                    )
                } else if (contentType?.contentType?.contains("octet") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.txt
                        )
                    )
                } else if (contentType?.contentType?.contains("doc") == true) {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.docx
                        )
                    )
                } else {
                    holder.iv_icon.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.document
                        )
                    )
                }
            }

        }

        private fun getModifiedDate(updatedTimeMillis: Long?): Any {
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
            return formatter.format(updatedTimeMillis)
        }

        override fun getItemCount(): Int {
            return fileList.size
        }

        fun getElementNameByIndex(map: LinkedHashMap<*, *>, index: Int): String {
            //return map[map.keys.toTypedArray()[index]].toString()
            return map.keys.toTypedArray()[index].toString()
        }

        fun getElementByIndex(
            map: LinkedHashMap<String, StorageMetadata>,
            index: Int
        ): StorageMetadata? {
            return map.values.toTypedArray()[index]
        }
    }

    companion object {
        fun openActivity(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }

        private const val RC_FILE_PICKER_PERM = 321
    }

    override fun onRefresh() {
        loadFileList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                // Get the Uri of the selected file
                val uri = data?.data
                val uriString = uri.toString()
                val myFile = File(uriString)
                val path = myFile.absolutePath
                var displayName: String? = null
                if (uriString.startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor = uri?.let { getContentResolver().query(it, null, null, null, null) }
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor!!.close()
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.name
                }
                if (uri != null && !TextUtils.isEmpty(displayName)) {
                    uploadFileToFirebase(uri, displayName)
                }
                Log.d(TAG, "onActivityResult: ${displayName}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
