package com.thoughtworks.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class WebImageView extends ImageView {

    public WebImageView(Context context) {
        super(context);
    }

    public WebImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUrl(String url) {
        new ImageDownloaderWithoutCache(this).execute(url);
    }
}
