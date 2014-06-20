package com.thoughtworks.android;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

import com.thoughtworks.mingle.murmurs.android.data.PaginatedMurmursCursor;

public class MatrixCursorLoader extends CursorLoader {
    private final PaginatedMurmursCursor cursor;

    public MatrixCursorLoader(Context context, PaginatedMurmursCursor cursor) {
        super(context);
        this.cursor = cursor;
    }

    @Override
    protected Cursor onLoadInBackground() {
        return cursor.withAtLeastOnePageLoaded();
    }
}
