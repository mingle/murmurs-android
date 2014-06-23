package com.thoughtworks.android.http;

import java.io.InputStream;

public interface ResponseHandler {
    void handleResponse(int responseCode, InputStream body);
}
