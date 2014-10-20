package com.thoughtworks.android.http;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.base.Preconditions;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DefaultFetcher implements Fetcher {
    private final DefaultHttpClient client;

    private static final Logger log = LogOMatic.getLogger(DefaultFetcher.class);

    private ResponseHandler successHandler;
    private ResponseHandler notFoundHandler;
    private String username;
    private String password;
    private ResponseHandler debugHandler;
    private ResponseHandler errorHandler;

    public DefaultFetcher() {
        client = new DefaultHttpClient();
        notFoundHandler = successHandler = new ResponseHandler() {
            @Override
            public void handleResponse(int responseCode, InputStream body) {
                throw new ResponseNotHandledException(responseCode);
            }
        };
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

    @Override
    public Fetcher basicAuth(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    @Override
    public Fetcher error(ResponseHandler handler) {
        this.errorHandler = handler;
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
            Preconditions.checkArgument(StringUtils.isNotEmpty(url));
            URI uri = URI.create(url);

            log.debug("GET request for " + uri);

            HttpGet request = new HttpGet(new URI(url));
            attachAnyInterceptors(uri);

            final HttpResponse response = client.execute(request);
            Header[] locationHeaders = response.getHeaders("location");
            if (locationHeaders.length > 0) {
                log.debug("Headers: " + locationHeaders);
            }

            int code = response.getStatusLine().getStatusCode();

            if (code >= 300 && this.errorHandler != null) {
                with(errorHandler).handleResponse(response);
            } else {
                switch (code) {
                    case 200:
                    case 201:
                    case 302:
                        with(successHandler).handleResponse(response);
                        break;
                    case 404:
                        with(notFoundHandler).handleResponse(response);
                        break;
                    default:
                        log.error("Unhandled response " + code);
                        for (Header h : response.getAllHeaders()) {
                            log.debugf("%s => %s", h.getName(), h.getValue());
                        }
                        log.debug(IOUtils.toString(response.getEntity().getContent()));
                        throw new ResponseNotHandledException(code);
                }
            }


        } catch (Exception e) {
            log.error("error",e);
            throw new RuntimeException("Error loading resource " + url, e);

        }
    }

    private void attachAnyInterceptors(URI uri) {
        if (username != null) {
            client.addRequestInterceptor(new BasicAuthRequestInterceptor(client, uri, username, password));
        }
    }


    private class ResponseNotHandledException extends RuntimeException {
        ResponseNotHandledException(int code) {
            super("No handler successfully handled HTTP " + code);
        }
    }
}
