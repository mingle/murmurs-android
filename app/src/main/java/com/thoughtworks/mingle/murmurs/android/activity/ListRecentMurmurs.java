package com.thoughtworks.mingle.murmurs.android.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.ocpsoft.pretty.time.PrettyTime;
import com.thoughtworks.android.MatrixCursorLoader;
import com.thoughtworks.android.AvatarImageView;
import com.thoughtworks.mingle.murmurs.android.R;
import com.thoughtworks.mingle.murmurs.android.data.PaginatedMurmursCursor;

import java.util.Date;


public class ListRecentMurmurs extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final Logger log = LogOMatic.getLogger(ListRecentMurmurs.class);

    private SimpleCursorAdapter cursorAdapter;

    private static final PrettyTime PRETTY_TIME = new PrettyTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        this.cursorAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.activity_list_single_murmur_summary, null, PaginatedMurmursCursor.COLUMN_NAMES, new int[]{0, R.id.author, R.id.createdAt, R.id.body, R.id.icon}, 0);
        this.cursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(columnIndex == PaginatedMurmursCursor.COLUMN_NAMES_LIST.indexOf(PaginatedMurmursCursor.ICON_PATH)) {
                    AvatarImageView imageView = (AvatarImageView) view.findViewById(R.id.icon);
                    String username = cursor.getString(1);
                    imageView.setUrl(username, cursor.getString(columnIndex));
                    return true;
                } else if (columnIndex == PaginatedMurmursCursor.COLUMN_NAMES_LIST.indexOf(PaginatedMurmursCursor.ICON_PATH)) {
                    String prettyCreatedAt = PRETTY_TIME.format(new Date(cursor.getLong(2)));
                    ((TextView) view).setText(prettyCreatedAt);
                    return true;
                }
                return false;
            }
        });
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
