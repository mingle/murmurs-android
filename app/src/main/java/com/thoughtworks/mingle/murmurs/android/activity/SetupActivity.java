package com.thoughtworks.mingle.murmurs.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.thoughtworks.android.Settings;
import com.thoughtworks.mingle.murmurs.android.R;

public class SetupActivity extends Activity {

    private static final Logger log = LogOMatic.getLogger(SetupActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
//        Button saveButton = (Button) findViewById(R.id.saveSettingsButton);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    public void saveSettings(View view) {
        Settings settings = Settings.under(getApplicationContext());
        settings.setUrl(((TextView)findViewById(R.id.project_url)).getText().toString());
        settings.setEmail(((TextView) findViewById(R.id.email)).getText().toString());
        settings.setPassword(((TextView) findViewById(R.id.password)).getText().toString());
        Intent intent = new Intent(this, MurmursFeed.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
