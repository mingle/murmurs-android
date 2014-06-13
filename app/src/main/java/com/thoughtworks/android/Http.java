package com.thoughtworks.android;

import android.app.Application;

import com.google.common.io.CharSource;

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
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
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
        public CharSource get(String url) {
            return Http.this.doGet(Http.this.client, url);
        }
    }

    private static final LogOMatic.Logger log = LogOMatic.getLogger(Http.class);

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

    public static CharSource get(String url) {
        Http self = new Http();
        return self.doGet(self.client, url);
    }

    CharSource doPost(HttpClient client, String url, Map<String, String> params) {
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

        if (Settings.isAppInDebugMode()) {
            return debugLoggedResponseAsCharSource(response);
        } else {
            return responseToCharSource(response);
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    } finally {
        if (reader != null) {
            try { reader.close(); } catch (Exception closeException) {}
        }
    }

}

    CharSource doGet(HttpClient client, String url) {
        try {
            URI uri = URI.create(url);

            log.debug("GET request for " + uri);

            HttpGet request = new HttpGet(new URI(url));
            final HttpResponse response = client.execute(request);
            Header[] locationHeaders = response.getHeaders("location");
            if (locationHeaders.length > 0) {
                log.debug("Headers: " + locationHeaders);
            }

            if (Settings.isAppInDebugMode()) {
                return debugLoggedResponseAsCharSource(response);
            } else {
                return responseToCharSource(response);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading resource " + url, e);
        }
    }

    private CharSource debugLoggedResponseAsCharSource(HttpResponse response) throws IOException  {

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 1024);
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        String line = null;
        while ((line = reader.readLine()) != null) {
            log.debug(line);
            writer.println(line);
        }
        return CharSource.wrap(writer.toString());
    }

    private CharSource responseToCharSource(final HttpResponse response) {
        return new CharSource() {
            @Override
            public Reader openStream() throws IOException {
                return new InputStreamReader(response.getEntity().getContent());
            }
        };
    }
}
