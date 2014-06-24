package com.thoughtworks.android.http;

public interface Fetcher {
    void get(String url);

    Fetcher success(ResponseHandler handler);

    Fetcher notFound(ResponseHandler handler);

    Fetcher basicAuth(String username, String password);
}
