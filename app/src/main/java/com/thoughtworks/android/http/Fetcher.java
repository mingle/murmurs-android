package com.thoughtworks.android.http;

import com.thoughtworks.mingle.api.hmac.HmacAuth;

public interface Fetcher {
    void get(String url);

    Fetcher success(ResponseHandler handler);

    Fetcher notFound(ResponseHandler handler);

    Fetcher basicAuth(String username, String password);

    Fetcher hmacAuth(HmacAuth auth);

    Fetcher error(ResponseHandler handler);
}
