package com.thoughtworks.mingle.murmurs.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.dephillipsdesign.lychee.base.MutableValueHolder;
import com.google.common.escape.Escapers;
import com.google.common.net.UrlEscapers;
import com.thoughtworks.android.Settings;
import com.thoughtworks.android.http.Http;
import com.thoughtworks.android.http.ResponseHandler;
import com.thoughtworks.mingle.murmurs.Murmur;
import com.thoughtworks.mingle.murmurs.MurmurPersistor;
import com.thoughtworks.mingle.murmurs.android.R;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class CreateMurmur extends Activity {

    private final static Logger log = LogOMatic.getLogger(CreateMurmur.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_murmur);
    }

    public void createMurmur(View view) {
        final Button button = (Button) view;
        final String oldText = button.getText().toString();
        button.setText("saving...");
        final Settings settings = Settings.under(getApplicationContext());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... ignored) {
                TextView input = (TextView) findViewById(R.id.murmurInput);
                String murmurText = input.getText().toString();
                Http.success(new ResponseHandler() {
                    public void handleResponse(int responseCode, InputStream httpBodyStream) {
                        startActivity(new Intent(CreateMurmur.this, MurmursFeed.class));
                        finish();
                    }
                }).error(new ResponseHandler() {
                    public void handleResponse(int responseCode, InputStream body)  {
                        log.error("HTTP " + responseCode);
                    }
                }).basicAuth(settings.getEmail(), settings.getPassword())
                    .post(settings.getMurmursUrl(), UrlEscapers.urlFormParameterEscaper().escape("murmur[body]=" + murmurText));
                return null;
            }
        }.execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_murmur, menu);
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
