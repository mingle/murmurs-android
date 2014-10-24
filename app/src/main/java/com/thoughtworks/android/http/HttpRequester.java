package com.thoughtworks.android.http;

public interface HttpRequester {
    void get(String url);

    HttpRequester success(ResponseHandler handler);

    HttpRequester notFound(ResponseHandler handler);

    HttpRequester basicAuth(String username, String password);

    HttpRequester error(ResponseHandler handler);

    void post(String url, String body);
}
