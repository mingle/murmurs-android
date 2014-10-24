package com.thoughtworks.android.http;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

public class DefaultHttpRequester implements HttpRequester {
    private final DefaultHttpClient client;

    private static final Logger log = LogOMatic.getLogger(DefaultHttpRequester.class);

    private ResponseHandler successHandler;
    private ResponseHandler notFoundHandler;
    private String username;
    private String password;
    private ResponseHandler debugHandler;
    private ResponseHandler errorHandler;

    public DefaultHttpRequester() {
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
    public HttpRequester success(ResponseHandler handler) {
        successHandler = handler;
        return this;
    }

    @Override
    public HttpRequester notFound(ResponseHandler handler) {
        notFoundHandler = handler;
        return this;
    }

    @Override
    public HttpRequester basicAuth(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    @Override
    public HttpRequester error(ResponseHandler handler) {
        this.errorHandler = handler;
        return this;
    }

    @Override
    public void post(String url, String body) {
        URI uri = URI.create(url);

        log.debug("POST request for " + uri);

        HttpPost request = new HttpPost(uri);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        try {
            request.setEntity(new StringEntity(body));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        attachAnyInterceptors(uri);

        try {
            handleResponse(client.execute(request));
        } catch (IOException e) {
            log.error("error",e);
            throw new RuntimeException("Error POSTing resource " + url + " with body " + body, e);
        }
    }

    private void doGet(String url) {
        try {
            URI uri = URI.create(url);

            log.debug("GET request for " + uri);

            HttpGet request = new HttpGet(new URI(url));
            attachAnyInterceptors(uri);
            handleResponse(client.execute(request));


        } catch (Exception e) {
            log.error("error",e);
            throw new RuntimeException("Error loading resource " + url, e);

        }
    }

    private void handleResponse(final HttpResponse response) throws IOException {
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
