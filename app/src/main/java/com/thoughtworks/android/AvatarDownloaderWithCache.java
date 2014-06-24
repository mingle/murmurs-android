package com.thoughtworks.android;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class AvatarDownloaderWithCache extends AvatarDownloaderWithoutCache {

    private static final Cache<String, Bitmap> cache;
    private static final Logger log = LogOMatic.getLogger(AvatarDownloaderWithCache.class);

       static {
           cache = CacheBuilder.newBuilder()
                   .maximumWeight(1024 * 1024)
                   .weigher(new Weigher<String, Bitmap>() {
                       public int weigh(String url, Bitmap bmp) {
                           return bmp.getByteCount();
                       }
                   })
                   .expireAfterWrite(2, TimeUnit.HOURS).build();
       }

    public AvatarDownloaderWithCache(String username, ImageView image) {
        super(username, image);
    }

    private Bitmap fetchFromWeb(String url) {
        return super.doInBackground(url);
    }

     public static class NoImageFoundException extends Exception {

        }

    @Override
    protected Bitmap doInBackground(String... params) {
        final String url = params[0];
        try {
            return cache.get(url, new Callable<Bitmap>() {
                public Bitmap call() throws Exception {
                    log.debug("Cache miss for " + url);
                    Bitmap bmp = AvatarDownloaderWithCache.this.fetchBitmapWithFallback(url);
                    if (bmp == null) {
                        throw new NoImageFoundException();
                    } else {
                        return bmp;
                    }
                }
            });
        } catch (ExecutionException e) {
            if (e.getCause().getClass().equals(NoImageFoundException.class)) {
                log.info("Could not find image for " + url);
                return null;
            }
            throw new RuntimeException(e);
        }
    }


}
