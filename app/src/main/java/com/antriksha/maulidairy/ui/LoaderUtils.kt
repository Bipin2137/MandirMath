package com.antriksha.maulidairy.ui

import android.content.Context
import com.abhaysapp.awesomeprogressdialog.AwesomeProgressDialog
import com.shashank.sony.fancytoastlib.FancyToast

class LoaderUtils(var context: Context) {


    private var progressDialog: AwesomeProgressDialog? = null

    fun showErrorToast(error: String?) {
        FancyToast.makeText(context, error, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
    }

    fun showSuccessToast(msg: String?) {
        FancyToast.makeText(context, msg, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
    }

    fun showLoader(title: String?) {
        progressDialog = AwesomeProgressDialog(context)
        progressDialog?.addTitle(title) // add your title here.
        progressDialog?.setStyle(AwesomeProgressDialog.STYLE_LOADING_SIMPLE)
        progressDialog?.isCancelable(false)
        progressDialog?.showDialog()
    }

    fun dismissLoader() {
        if (progressDialog != null) {
            progressDialog?.dismissDialog()
        }
    }
}