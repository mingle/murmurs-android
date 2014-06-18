package com.thoughtworks.android;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Translates apache commons HTTP to guava-friendly
 */
public class Http {

    public class HttpForChaining {
        public ByteSource get(String url) {
            return Http.this.doGet(Http.this.client, url);
        }
    }

    private static final Logger log = LogOMatic.getLogger(Http.class);

    private final HttpClient client;

    Http() {
        this.client = new DefaultHttpClient();
    }

    Http(HttpClient client) {
        this.client = client;
    }

    public static HttpForChaining withBasicAuth(String username, String password) {
        Http self = new Http();
        return self.new HttpForChaining();
    }

    public static ByteSource get(String url) {
        Http self = new Http();
        return self.doGet(self.client, url);
    }

    ByteSource doPost(HttpClient client, String url, Map<String, String> params) {
        URI uri = URI.create(url);
        log.debug("POST to " + uri);
        log.debug("POST parmas: " + params + " to " + url);

        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (String name : params.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(name, params.get(name)));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }

        BufferedReader reader = null;
        try{
            final HttpResponse response = client.execute(httpPost);

        return responseToByteSource(response);

    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        if (reader != null) {
            try { reader.close(); } catch (Exception closeException) {}
        }
    }

}

    ByteSource doGet(HttpClient client, String url) {
        try {
            URI uri = URI.create(url);

            log.debug("GET request for " + uri);

            HttpGet request = new HttpGet(new URI(url));
            final HttpResponse response = client.execute(request);
            Header[] locationHeaders = response.getHeaders("location");
            if (locationHeaders.length > 0) {
                log.debug("Headers: " + locationHeaders);
            }

            return responseToByteSource(response);

        } catch (Exception e) {
            throw new RuntimeException("Error loading resource " + url, e);
        }
    }

    private ByteSource responseToByteSource(final HttpResponse response) {
        return new ByteSource() {
            @Override
            public InputStream openStream() throws IOException {
                return new BOMInputStream(response.getEntity().getContent());
            }
        };
    }
}
