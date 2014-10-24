package com.thoughtworks.mingle.murmurs.android.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.ocpsoft.pretty.time.PrettyTime;
import com.thoughtworks.android.MatrixCursorLoader;
import com.thoughtworks.android.AvatarImageView;

import com.thoughtworks.android.Utc;

import com.thoughtworks.mingle.murmurs.android.R;
import com.thoughtworks.mingle.murmurs.android.data.DataLoadErrorHandler;
import com.thoughtworks.mingle.murmurs.android.data.PaginatedMurmursCursor;

import java.util.Date;


public class MurmursFeed extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>, DataLoadErrorHandler {

    private static final Logger log = LogOMatic.getLogger(MurmursFeed.class);

    private SimpleCursorAdapter cursorAdapter;

    private static final PrettyTime PRETTY_TIME = new PrettyTime();

    private int lastId = 1;

    @Override
    protected void onPause() {
        super.onPause();
        log.debug("onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillInData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_recent_murmurs);
    }

    public void openCreateMurmurActivity(View view) {
        startActivity(new Intent(this, CreateMurmur.class));
    }

    private void fillInData() {
        this.cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.activity_list_single_murmur_summary, null, PaginatedMurmursCursor.COLUMN_NAMES, new int[]{0, R.id.author, R.id.createdAt, R.id.body, R.id.icon}, 0);
        this.cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                switch (columnIndex) {
                    case 4:
                        AvatarImageView imageView = (AvatarImageView) view.findViewById(R.id.icon);
                        String username = cursor.getString(1);
                        imageView.setUrl(username, cursor.getString(columnIndex));
                        return true;
                    case 2:
                        final Date createdAt = new Date(cursor.getLong(2));
                        final Date corrected = Utc.toLocalTime(createdAt);
                        String prettyCreatedAt = PRETTY_TIME.format(corrected);
                        ((TextView) view).setText(prettyCreatedAt);
                        return true;
                }
                return false;
            }
        });
        setListAdapter(this.cursorAdapter);
        getLoaderManager().initLoader(lastId++, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_recent_murmurs, menu);
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MatrixCursorLoader(this.getApplicationContext(), new PaginatedMurmursCursor(getApplicationContext(), this));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        log.debug("onLoadFinished for " + cursor);
        this.cursorAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        this.cursorAdapter.swapCursor(null);
    }

    @Override
    public void handleDataLoadError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CharSequence text = String.format("Unable to load Murmurs due to %s.  Check your settings.", msg);
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.show();
            }
        });

    }
}
