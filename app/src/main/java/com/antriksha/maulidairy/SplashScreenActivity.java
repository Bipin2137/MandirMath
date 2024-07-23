package com.antriksha.maulidairy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.antriksha.maulidairy.utils.SPrefs;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private int permissionAcceptCount = 0;
    private String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {

            finish();
            return;
        }
        startAnimating();
    }

    // The thread to wait for splash screen events
    private void startAnimating() {
        // Load animations for all views within the LinearLayout
        ImageView logo = (ImageView) findViewById(R.id.imageView1);
        Animation spin = AnimationUtils.loadAnimation(this, R.anim.custom_anim);
        logo.startAnimation(spin);

        spin.setAnimationListener(new AnimationListener() {

            public void onAnimationEnd(Animation animation) {
                gotoNextActivity();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {

            }
        });
    }

    private void gotoNextActivity() {
        if (TextUtils.isEmpty(SPrefs.getString(this, SPrefs.KEY_PASSWORD))) {
            startActivity(new Intent(SplashScreenActivity.this, SetPasswordActivity.class));
            SplashScreenActivity.this.finish();
        } else {
            startActivity(new Intent(SplashScreenActivity.this, PasswordActivity.class));
            SplashScreenActivity.this.finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LinearLayout ll = (LinearLayout) findViewById(R.id.imageLayout);
        ll.clearAnimation();
    }
}

