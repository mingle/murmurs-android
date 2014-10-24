package com.thoughtworks.android.http;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;


public class Http {

    private Http() { }

    private static final Logger log = LogOMatic.getLogger(Http.class);

    public static HttpRequester success(ResponseHandler handler) {
        return new DefaultHttpRequester().success(handler);
    }


}

