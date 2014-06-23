package com.thoughtworks.android.http;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.base.Optional;


public class Http {

    private Http() { }

    private static final Logger log = LogOMatic.getLogger(Http.class);

    public static Fetcher success(ResponseHandler handler) {
        return new DefaultFetcher().success(handler);
    }


}

