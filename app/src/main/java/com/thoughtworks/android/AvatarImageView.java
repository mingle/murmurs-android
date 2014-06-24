package com.thoughtworks.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AvatarImageView extends ImageView {

    public AvatarImageView(Context context) {
        super(context);
    }

    public AvatarImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setUrl(String username, String url) {
        new AvatarDownloaderWithCache(username, this).execute(url);
    }


}
