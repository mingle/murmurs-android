package com.thoughtworks.mingle.murmurs.android.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.thoughtworks.android.MatrixCursorLoader;
import com.thoughtworks.mingle.murmurs.android.R;
import com.thoughtworks.mingle.murmurs.android.data.PaginatedMurmursCursor;


public class ListRecentMurmurs extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger log = LogOMatic.getLogger(ListRecentMurmurs.class);

    private SimpleCursorAdapter cursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        this.cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.activity_list_recent_murmurs, null, PaginatedMurmursCursor.COLUMN_NAMES, null, 0);
        setListAdapter(this.cursorAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_recent_murmurs, menu);
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new MatrixCursorLoader(this.getApplicationContext(), new PaginatedMurmursCursor());
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
}
