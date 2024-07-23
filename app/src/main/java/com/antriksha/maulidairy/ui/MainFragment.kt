package com.antriksha.maulidairy.ui

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.antriksha.maulidairy.BaseActivity
import com.antriksha.maulidairy.BuildConfig
import com.antriksha.maulidairy.CreateFileActivity
import com.antriksha.maulidairy.ImageViewActivity
import com.antriksha.maulidairy.R
import com.antriksha.maulidairy.utils.DecryptionCallback
import com.antriksha.maulidairy.utils.EncryptionCallback
import com.antriksha.maulidairy.utils.FileEncryptionUtility
import com.antriksha.maulidairy.utils.FirebaseStorageUtils
import com.antriksha.maulidairy.utils.QRCodeENCDEC
import com.antriksha.maulidairy.utils.SPrefs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.StorageMetadata
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "pathList"
private const val ARG_PARAM2 = "param2"

class MainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var mContext: Context
    private val TAG = "MainFragment"
    private var firebaseStorageUtils: FirebaseStorageUtils? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var fileAdapter: FileAdapter? = null
    private var fileList: LinkedHashMap<String, StorageMetadata?>? = null
    private var pathList: ArrayList<String>? = null
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mSwipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_layout)
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val tv_label = view.findViewById<TextView>(R.id.tv_label)
        val back = view.findViewById<ImageView>(R.id.back)
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        fileList = LinkedHashMap()

        back.setOnClickListener {
            activity?.onBackPressed()
        }

        recyclerView?.apply {
            layoutManager = GridLayoutManager(mContext, 3)
            setHasFixedSize(true)
            adapter = fileAdapter
        }

        mSwipeRefreshLayout?.setOnRefreshListener { }
        mSwipeRefreshLayout?.setColorSchemeResources(
            android.R.color.holo_red_light,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        mSwipeRefreshLayout?.post(object : Runnable {
            override fun run() {
            }
        })

        // Set item click listener for BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.createfile -> {
                    CreateFileActivity.openActivity(mContext, path)
                    true
                }

                R.id.createfolder -> {

                    true
                }

                R.id.upload -> {
                    true
                }

                else -> false
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {
                   // putSerializable(ARG_PARAM1, pathList)
                }
            }
    }

}