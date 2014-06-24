package com.thoughtworks.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.dephillipsdesign.lychee.base.MutableValueHolder;
import com.google.common.base.CharMatcher;
import com.thoughtworks.android.http.Http;
import com.thoughtworks.android.http.ResponseHandler;

import java.io.InputStream;


public class AvatarDownloaderWithoutCache extends AsyncTask<String, Void, Bitmap> {

    private final ImageView image;
    private final String username;

    private static final Logger log = LogOMatic.getLogger(AvatarDownloaderWithoutCache.class);

    public AvatarDownloaderWithoutCache(String username, ImageView image) {
        this.username = username;
        this.image = image;
    }

    protected Bitmap doInBackground(String... params) {
        return fetchBitmapWithFallback(params[0]);
    }

    private ResponseHandler getSuccessHandler(final MutableValueHolder<Bitmap> valueHolder) {
        return new ResponseHandler() {
            @Override
            public void handleResponse(int responseCode, InputStream body) {
                valueHolder.set(BitmapFactory.decodeStream(body, null, null));
            }
        };
    }

    protected Bitmap fetchBitmapWithFallback(String url) {
        final MutableValueHolder<Bitmap> bitmap = new MutableValueHolder<Bitmap>();
        Http
           .success(getSuccessHandler(bitmap))
           .notFound(new ResponseHandler() {
               @Override
               public void handleResponse(int responseCode, InputStream body) {
                   String initial = username.toLowerCase().substring(0, 1);
                   log.debugf("Attempting to retrieve fallback icon for '%s',  initial: %s", username, initial);
                   if (CharMatcher.inRange('a', 'z').matchesAllOf(initial)) {
                       bitmap.set(fetchBitmap(Settings.under(image.getContext()).getFallbackIconUrl(initial)));
                   }
               }
           }).get(url);
        return bitmap.get().orNull();
    }

    private Bitmap fetchBitmap(String url) {
        final MutableValueHolder<Bitmap> bitmap = new MutableValueHolder<Bitmap>();
        Http.success(getSuccessHandler(bitmap)).get(url);
        return bitmap.get().orNull();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            image.setBackgroundColor(Color.parseColor(BackgroundColor.forUser(username)));
            image.setImageBitmap(bitmap);
        }
    }
}
