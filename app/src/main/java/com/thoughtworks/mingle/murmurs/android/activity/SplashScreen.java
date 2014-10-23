package com.thoughtworks.mingle.murmurs.android.activity;

import com.thoughtworks.android.Settings;
import com.thoughtworks.mingle.murmurs.android.activity.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import com.thoughtworks.mingle.murmurs.android.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Settings settings = Settings.under(getApplicationContext());

        Intent intent;
        if (settings.seemIncomplete()) {
            intent = new Intent(this, SetupActivity.class);
        } else {
            intent = new Intent(this, MurmursFeed.class);
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivity(new Intent(this, SetupActivity.class));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

}
