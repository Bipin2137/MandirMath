package com.antriksha.maulidairy;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abhaysapp.awesomeprogressdialog.AwesomeProgressDialog;
import com.shashank.sony.fancytoastlib.FancyToast;


/**
 * Created by BIPIN on 2/8/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    public Context mContext;
    AwesomeProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected void showErrorToast(String error) {
        FancyToast.makeText(this, error, FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
    }

    protected void showSuccessToast(String msg) {
        FancyToast.makeText(this, msg, FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
    }

    protected void showLoader(Context context, String title) {
        progressDialog = new AwesomeProgressDialog(context);
        progressDialog.addTitle(title); // add your title here.
        progressDialog.setStyle(AwesomeProgressDialog.STYLE_LOADING_SIMPLE);
        progressDialog.isCancelable(false);
        progressDialog.showDialog();
    }

    protected void dismissLoader() {
        if (progressDialog != null) {
            progressDialog.dismissDialog();
        }
    }
}
