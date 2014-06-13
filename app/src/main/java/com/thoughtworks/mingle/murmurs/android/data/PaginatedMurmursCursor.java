package com.thoughtworks.mingle.murmurs.android.data;

import android.database.MatrixCursor;

import com.thoughtworks.android.Http;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.CharSource;
import com.thoughtworks.android.Settings;
import com.thoughtworks.mingle.murmurs.Murmur;
import com.thoughtworks.mingle.murmurs.MurmursLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PaginatedMurmursCursor extends MatrixCursor {

    private static final java.lang.String[] COLUMN_NAMES = { "_ID", "TAGLINE", "BODY", "ICON_PATH" };

    public PaginatedMurmursCursor() {
        super(COLUMN_NAMES);
    }

    public PaginatedMurmursCursor prepopulateFirstPage() {

        CharSource xml = Http.get(Settings.getMurmursIndexUrl());
        List<Murmur> murmurs = new MurmursLoader().loadMultipleFromXml(xml);

        Collection<Collection<String>> columnValues = Collections2.transform(murmurs, new Function<Murmur, Collection<String>>() {
            @Override
            public Collection<String> apply(Murmur murmur) {
                return new ArrayList<String>(Arrays.asList(String.valueOf(murmur.getId()), murmur.getTagline(), murmur.getShortBody(), murmur.getIconPathUri()));
            }
        });

        for(Iterable<String> columnValue : columnValues) {
            addRow(columnValue);
        }
        return this;
    }
}
