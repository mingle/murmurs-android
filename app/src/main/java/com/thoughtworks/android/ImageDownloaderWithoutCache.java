package com.thoughtworks.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;


public class ImageDownloaderWithoutCache extends AsyncTask<String, Void, Bitmap> {

    private final ImageView image;

    public ImageDownloaderWithoutCache(ImageView image) {
        this.image = image;
    }

    protected Bitmap doInBackground(String... params) {
        final Http.MutableValueHolder<Bitmap> bitmap = new Http.MutableValueHolder<Bitmap>();
        String uri = params[0];
        Http.success(new Http.ResponseHandler() {
            @Override
            public void handleResponse(int responseCode, InputStream body) {
                bitmap.set(BitmapFactory.decodeStream(body, null, null));
            }
        }).notFound(new Http.ResponseHandler() {
            @Override
            public void handleResponse(int responseCode, InputStream body) {
                //do nothing
            }
        }).get(uri);
        return bitmap.get().orNull();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }
    }
}
