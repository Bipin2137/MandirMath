package com.antriksha.maulidairy

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.antriksha.maulidairy.ui.FragmentHolderActivity
import com.antriksha.maulidairy.utils.QRCodeENCDEC
import com.antriksha.maulidairy.utils.SPrefs
import com.chaos.view.PinView
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.concurrent.Executor

class PasswordActivity : BaseActivity() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var executor: Executor
    private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private var keyguardManager: KeyguardManager? = null
    private var passwordEditText: EditText? = null
    private var confirmButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        passwordEditText = findViewById<PinView>(R.id.passwordEditText)
        confirmButton = findViewById<Button>(R.id.confirmButton)
        init()
        checkDeviceCanAuthenticateWithBiometrics()
        confirmButton?.setOnClickListener {
            checkLogin()
        }
    }

    private fun checkLogin() {
        val password = passwordEditText?.text.toString()

        if (TextUtils.isEmpty(password)) {
            FancyToast.makeText(this,"Please enter password",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show()
            return
        }

        val enc = QRCodeENCDEC()
        val decrypt = enc.decrypt(SPrefs.get(this@PasswordActivity, SPrefs.KEY_PASSWORD), BuildConfig.ENC_KEY)

        if (password.equals(decrypt)) {
            authenticateWithBiometrics()
        } else {
            FancyToast.makeText(this,"Password does not match",FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show()
        }
    }

    private fun init() {
        executor = ContextCompat.getMainExecutor(this)
        callBack = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@PasswordActivity, getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@PasswordActivity, getString(R.string.message_success), Toast.LENGTH_LONG).show()
                FragmentHolderActivity.openActivity(this@PasswordActivity)
                finish()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@PasswordActivity, getErrorMessage(errorCode), Toast.LENGTH_LONG).show()
            }
        }
        biometricPrompt =
            androidx.biometric.BiometricPrompt(this@PasswordActivity, executor, callBack)
    }

    private fun checkDeviceCanAuthenticateWithBiometrics() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, getString(R.string.message_no_support_biometrics), Toast.LENGTH_LONG).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, getString(R.string.message_no_hardware_available), Toast.LENGTH_LONG).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                checkAPILevelAndProceed()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Toast.makeText(this, getString(R.string.error_security_update_required), Toast.LENGTH_LONG).show()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Toast.makeText(this, getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun biometricsEnrollIntent(): Intent {
        return Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        }
    }

    private fun setUpDeviceLockInAPIBelow23Intent(): Intent {
        return Intent(Settings.ACTION_SECURITY_SETTINGS)
    }

    private fun checkAPILevelAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startActivityForResult(setUpDeviceLockInAPIBelow23Intent(), RC_DEVICE_CREDENTIAL_ENROLL)
        } else {
            startActivityForResult(biometricsEnrollIntent(), RC_BIOMETRICS_ENROLL)
        }
    }

    private fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                getString(R.string.message_user_app_authentication)
            }
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                getString(R.string.error_hw_unavailable)
            }
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
                getString(R.string.error_unable_to_process)
            }
            BiometricPrompt.ERROR_TIMEOUT -> {
                getString(R.string.error_time_out)
            }
            BiometricPrompt.ERROR_NO_SPACE -> {
                getString(R.string.error_no_space)
            }
            BiometricPrompt.ERROR_CANCELED -> {
                getString(R.string.error_canceled)
            }
            BiometricPrompt.ERROR_LOCKOUT -> {
                getString(R.string.error_lockout)
            }
            BiometricPrompt.ERROR_VENDOR -> {
                getString(R.string.error_vendor)
            }
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                getString(R.string.error_lockout_permanent)
            }
            BiometricPrompt.ERROR_USER_CANCELED -> {
                getString(R.string.error_user_canceled)
            }
            BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                checkAPILevelAndProceed()
                getString(R.string.error_no_biometrics)
            }
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                getString(R.string.error_hw_not_present)
            }
            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                startActivityForResult(biometricsEnrollIntent(), RC_BIOMETRICS_ENROLL)
                getString(R.string.error_no_device_credentials)
            }
            BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> {
                getString(R.string.error_security_update_required)
            }
            else -> {
                getString(R.string.error_unknown)
            }
        }
    }

    private fun authenticateWithBiometrics() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(getString(R.string.title_biometric_dialog))
            setDescription(getString(R.string.text_description_biometrics_dialog))
            setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        }.build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager?.let { manager ->
                if (manager.isKeyguardSecure) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    startActivityForResult(setUpDeviceLockInAPIBelow23Intent(), RC_DEVICE_CREDENTIAL_ENROLL)
                }
            }
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    companion object {
        const val RC_BIOMETRICS_ENROLL = 10
        const val RC_DEVICE_CREDENTIAL_ENROLL = 18
    }
}