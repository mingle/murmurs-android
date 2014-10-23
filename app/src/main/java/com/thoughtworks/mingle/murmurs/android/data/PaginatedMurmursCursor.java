package com.thoughtworks.mingle.murmurs.android.data;

import android.content.Context;
import android.database.MatrixCursor;


import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.io.CharSource;
import com.thoughtworks.android.http.Http;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;


import com.thoughtworks.android.Settings;
import com.thoughtworks.android.http.ResponseHandler;
import com.thoughtworks.mingle.murmurs.MurmursLoader;
import com.thoughtworks.mingle.murmurs.Murmur;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class PaginatedMurmursCursor extends MatrixCursor {

    private static final Logger log = LogOMatic.getLogger(PaginatedMurmursCursor.class);
    public static final String ID = "_ID";
    public static final String AUTHOR = "AUTHOR";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String SHORT_BODY = "SHORT_BODY";
    public static final String ICON_PATH = "ICON_PATH";
    public static final java.lang.String[] COLUMN_NAMES = {ID, AUTHOR, CREATED_AT, SHORT_BODY, ICON_PATH};
    private final Context context;
    private final DataLoadErrorHandler dataLoadErrorHandler;

    public PaginatedMurmursCursor(Context context, DataLoadErrorHandler handler) {
        super(COLUMN_NAMES);
        this.context = context;
        this.dataLoadErrorHandler = handler;
    }

    private ResponseHandler loadMurmursFromXml() {
        return new ResponseHandler() {
            @Override
            public void handleResponse(int responseCode, final InputStream body) {

                final long start = System.currentTimeMillis();

                List<Murmur> murmurs = new MurmursLoader().loadMultipleFromXml(new CharSource() {
                    @Override
                    public Reader openStream() throws IOException {
                        return new InputStreamReader(body);
                    }
                });

                Collection < Collection < Object >> columnValues = Collections2.transform(murmurs, new Function<Murmur, Collection<Object>>() {
                    @Override
                    public Collection<Object> apply(Murmur murmur) {
                        log.debugf("Loading murmur '%s' into Cursor", murmur.getShortBody());
                        return new ArrayList<Object>(Arrays.asList(murmur.getId(), murmur.getAuthor(), murmur.getCreatedAt().getTime(), murmur.getShortBody(), murmur.getIconPathUri()));
                    }
                });

                long end = System.currentTimeMillis();
                log.infof("Retrieved %d murmurs in %fsec", columnValues.size(), ((end - start) / 1000.0));
                for(Iterable<Object> columnValue : columnValues) {
                    addRow(columnValue);
                }

            }
        };
    }

    public PaginatedMurmursCursor withAtLeastOnePageLoaded() {

        if (getCount() == 0) {
            log.debug("prepopulating first page or murmurs");
            try {
                Settings settings = Settings.under(context);
                Http.success(loadMurmursFromXml()).hmacAuth(settings.getAuth()).error(new ResponseHandler() {
                    @Override
                    public void handleResponse(int responseCode, InputStream body) {
                        dataLoadErrorHandler.handleDataLoadError("HTTP " + responseCode);
                    }
                }).get(settings.getMurmursUrl());
            } catch (Exception e) {
                dataLoadErrorHandler.handleDataLoadError("Internal error: " + e.getMessage());
            }

        }

        return this;
    }
}
