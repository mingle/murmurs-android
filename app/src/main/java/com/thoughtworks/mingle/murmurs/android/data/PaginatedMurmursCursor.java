package com.thoughtworks.mingle.murmurs.android.data;

import android.database.MatrixCursor;


import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.thoughtworks.android.Http;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;


import com.thoughtworks.android.Settings;
import com.thoughtworks.mingle.murmurs.MurmursLoader;
import com.thoughtworks.mingle.murmurs.Murmur;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PaginatedMurmursCursor extends MatrixCursor {

    private static final Logger logger = LogOMatic.getLogger(PaginatedMurmursCursor.class);
    public static final java.lang.String[] COLUMN_NAMES = { "_ID", "TAGLINE", "BODY", "ICON_PATH" };

    public PaginatedMurmursCursor() {
        super(COLUMN_NAMES);

    }

    public PaginatedMurmursCursor prepopulateFirstPage() {

        logger.debug("prepopulating first page");
        long start = System.currentTimeMillis();

        final ByteSource xml = Http.get(Settings.getMurmursIndexUrl());

        List<Murmur> murmurs = new MurmursLoader().loadMultipleFromXml(new CharSource() {
            @Override
            public Reader openStream() throws IOException {
                return new InputStreamReader(xml.openBufferedStream());
            }
        });

                Collection < Collection < String >> columnValues = Collections2.transform(murmurs, new Function<Murmur, Collection<String>>() {
                    @Override
                    public Collection<String> apply(Murmur murmur) {
                        logger.debugf("Loading murmur '%s' into Cursor", murmur.getShortBody());
                        return new ArrayList<String>(Arrays.asList(String.valueOf(murmur.getId()), murmur.getTagline(), murmur.getShortBody(), murmur.getIconPathUri()));
                    }
                });

        long end = System.currentTimeMillis();
        logger.infof("Retrieved %d murmurs in %fsec", columnValues.size(), ((end - start) / 1000.0));
        for(Iterable<String> columnValue : columnValues) {
            addRow(columnValue);
        }
        return this;
    }
}
