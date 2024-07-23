package com.antriksha.maulidairy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.antriksha.maulidairy.BuildConfig
import com.antriksha.maulidairy.R
import com.antriksha.maulidairy.model.GabharaModel
import com.antriksha.maulidairy.utils.QRCodeENCDEC
import com.antriksha.maulidairy.utils.SPrefs
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.squareup.moshi.Moshi
import org.json.JSONObject


class FragmentHolderActivity : AppCompatActivity() {

    private var password = ""
    private val TAG = "FragmentHolderActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_holder)
        password = QRCodeENCDEC().decrypt(
            SPrefs.getString(this, SPrefs.KEY_PASSWORD),
            BuildConfig.ENC_KEY
        )

        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val mandirDetails = remoteConfig["mandirDetails"]
                    val json = JSONObject(mandirDetails.asString())
                    Toast.makeText(
                        this@FragmentHolderActivity,
                        json.toString(),
                        Toast.LENGTH_SHORT,
                    ).show()

                    val moshi = Moshi.Builder().build()
//                    val gabharaList =
                    val adapter = moshi.adapter<List<GabharaModel>>(GabharaModel::class.java)
                    val cards: List<GabharaModel>? = adapter.fromJson(json.toString())


                } else {
                    Toast.makeText(
                        this,
                        "Fetch failed",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

        replaceFragment()
    }

    private fun replaceFragment() {
        //val main_container = findViewById<FragmentContainerView>(R.id.main_container)
        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        /*transaction.replace(
            R.id.main_container,
            MainFragment.newInstance(pathList, object: MainFragment.UpdatePathListener {
                override fun onUpdate(value: String) {
                    pathList.add(value)
                    replaceFragment()
                }
            })
        )*/

        supportFragmentManager.findFragmentById(R.id.main_container)?.let { transaction.hide(it) };
        transaction.add(R.id.main_container, MainFragment.newInstance())
        transaction.addToBackStack(null);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit()
    }

    companion object {
        fun openActivity(context: Context) {
            val intent = Intent(context, FragmentHolderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }
}