package com.antriksha.maulidairy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import com.antriksha.maulidairy.ui.FragmentHolderActivity
import com.antriksha.maulidairy.utils.QRCodeENCDEC
import com.antriksha.maulidairy.utils.SPrefs
import com.shashank.sony.fancytoastlib.FancyToast

class SetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_password)

        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val confirmButton = findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (TextUtils.isEmpty(password)) {
                FancyToast.makeText(this,"Please enter password",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show()
                return@setOnClickListener
            }

            if (password.length <= 3) {
                FancyToast.makeText(this,"Please length should be minimum 4 letters",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show()
                return@setOnClickListener
            }

            if (password.equals(confirmPassword)) {
                val enc = QRCodeENCDEC()
                SPrefs.set(this@SetPasswordActivity, SPrefs.KEY_PASSWORD, enc.encrypt(password, BuildConfig.ENC_KEY))
                FancyToast.makeText(this,"Password set successfully",FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,true).show()
                FragmentHolderActivity.openActivity(this@SetPasswordActivity)
            } else {
                FancyToast.makeText(this,"Password does not match",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show()
            }
        }

    }
}