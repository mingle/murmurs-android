package com.thoughtworks.android.http;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DefaultFetcher implements Fetcher {
    private final HttpClient client;

    private static final Logger log = LogOMatic.getLogger(DefaultFetcher.class);

    private ResponseHandler successHandler;
    private ResponseHandler notFoundHandler;

    public DefaultFetcher() {
        client = new DefaultHttpClient();
    }

    @Override
    public void get(String url) {
        doGet(url);
    }

    @Override
    public Fetcher success(ResponseHandler handler) {
        successHandler = handler;
        return this;
    }

    @Override
    public Fetcher notFound(ResponseHandler handler) {
        notFoundHandler = handler;
        return this;
    }

    private org.apache.http.client.ResponseHandler<Void> with(final ResponseHandler userCallback) {
        return new org.apache.http.client.ResponseHandler<Void>() {
            @Override
            public Void handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
                InputStream body = null;
                try {
                    body = new BOMInputStream(httpResponse.getEntity().getContent());
                    userCallback.handleResponse(httpResponse.getStatusLine().getStatusCode(), body);
                    return null;
                } finally {
                    IOUtils.closeQuietly(body);
                }
            }

        };
    }

    private void doGet(String url) {
        try {

            URI uri = URI.create(url);

            log.debug("GET request for " + uri);

            HttpGet request = new HttpGet(new URI(url));
            final HttpResponse response = client.execute(request);
            Header[] locationHeaders = response.getHeaders("location");
            if (locationHeaders.length > 0) {
                log.debug("Headers: " + locationHeaders);
            }

            int code = response.getStatusLine().getStatusCode();
            if (code >= 200 && code < 300) {
                with(successHandler).handleResponse(response);
            } else if (code == 404) {
                with(notFoundHandler).handleResponse(response);
            }


        } catch (Exception e) {
            throw new RuntimeException("Error loading resource " + url, e);
        }
    }

    ;

    private class ResponseNotHandledException extends RuntimeException {
        ResponseNotHandledException(int code) {
            super("No handler successfully handled HTTP " + code);
        }
    }
}
