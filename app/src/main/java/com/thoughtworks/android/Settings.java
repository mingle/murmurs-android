package com.thoughtworks.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.thoughtworks.mingle.api.MingleInstance;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Settings {

    private final static Logger log = LogOMatic.getLogger(Settings.class);

    private static final ConcurrentHashMap<Context, Settings> SAVED_SETTINGS = new ConcurrentHashMap<Context, Settings>();
    private final SharedPreferences persistentPrefs;
    private String url;
    private String password;
    private String email;

    private Settings(SharedPreferences existingPreferences) {
       this.persistentPrefs = existingPreferences;
       this.email = existingPreferences.getString("email", "");
       this.password = existingPreferences.getString("password", "");
       this.url = existingPreferences.getString("url", "");
    }

    public void save() {
        SharedPreferences.Editor editor = this.persistentPrefs.edit();
        editor.putString("email", this.email);
        editor.putString("password", this.password);
        editor.putString("url", this.url);
        editor.commit();
    }

    public static Settings under(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Settings.class.getCanonicalName(), Context.MODE_PRIVATE);
        SAVED_SETTINGS.putIfAbsent(context, new Settings(preferences));
        return SAVED_SETTINGS.get(context);
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

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

   public boolean seemIncomplete() {
        log.debugf("Settings: URL(%s), email(%s), password(%s)", url, getEmail(), getMaskedPassword());
        return StringUtils.isBlank(url) || StringUtils.isBlank(getEmail()) || StringUtils.isBlank(getPassword());
    }

    private String getMaskedPassword() {
        return getPassword().replaceAll(".","*");
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
