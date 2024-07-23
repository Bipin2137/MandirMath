package com.antriksha.maulidairy.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.antriksha.maulidairy.R

class CustomDialog(private var context: Context, private val callback: (String) -> Unit) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setContentView(R.layout.folder_dialog)

        val editText = findViewById<EditText>(R.id.et_foldername)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener {
            val userInput = editText.text.toString()
            if (TextUtils.isEmpty(userInput)) {
                LoaderUtils(context).showErrorToast("Please enter folder name")
                return@setOnClickListener
            }
            callback(userInput)
            dismiss()
        }
    }
}
