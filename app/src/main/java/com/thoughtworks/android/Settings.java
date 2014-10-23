package com.thoughtworks.android;

import android.content.Context;
import android.content.SharedPreferences;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.thoughtworks.mingle.api.MingleInstance;
import com.thoughtworks.mingle.api.hmac.HmacAuth;

import org.apache.commons.lang.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

public class Settings {

    private final static Logger log = LogOMatic.getLogger(Settings.class);

    private static final ConcurrentHashMap<Context, Settings> SAVED_SETTINGS = new ConcurrentHashMap<Context, Settings>();
    private final SharedPreferences persistentPrefs;
    private String url;
    private HmacAuth auth;

    private Settings(SharedPreferences existingPreferences) {
       this.persistentPrefs = existingPreferences;
       this.url = existingPreferences.getString("url", "");
    }

    public void save() {
        SharedPreferences.Editor editor = this.persistentPrefs.edit();
        editor.putString("url", this.url);
        editor.commit();
    }

    public static Settings under(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Settings.class.getCanonicalName(), Context.MODE_PRIVATE);
        SAVED_SETTINGS.putIfAbsent(context, new Settings(preferences));
        return SAVED_SETTINGS.get(context);
    }

    public HmacAuth getAuth() {
        return MingleInstance.at(url).getAuth();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMurmursUrl() {
        return MingleInstance.at(url).getMurmursUrl();
    }


    public String getFallbackIconUrl(String initial) {
        return MingleInstance.at(url).getAvatarImageUrl(initial.toLowerCase() + ".png");
    };

   public boolean seemIncomplete() {
        log.debugf("Settings: URL(%s)", url);
        return StringUtils.isBlank(url);
    }
}
