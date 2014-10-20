package com.thoughtworks.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.thoughtworks.mingle.api.MingleInstance;

import org.apache.commons.lang.StringUtils;

public class Settings {

    private final static Logger log = LogOMatic.getLogger(Settings.class);

    private Settings() {

    }

    public static Settings under(Context context) {
        return new Settings();
    }

    private String getUrl() {
        return "https://murmurs-android-test.mingle-staging.thoughtworks.com/projects/test";
    }

    public String getMurmursUrl() {
        return MingleInstance.at(getUrl()).getMurmursUrl();
    }


    public String getFallbackIconUrl(String initial) {
        return MingleInstance.at(getUrl()).getAvatarImageUrl(initial.toLowerCase() + ".png");
    };



    public String getEmail() {
        return preferences.getString("email", "");
    }

    public String getPassword() {
        return preferences.getString("password", "");
    }

   public boolean seemIncomplete() {
        log.debugf("Settings: URL(%s), email(%s), password(%s)", getUrl(), getEmail(), getMaskedPassword());
        return StringUtils.isBlank(getUrl()) || StringUtils.isBlank(getEmail()) || StringUtils.isBlank(getPassword());
    }

    private String getMaskedPassword() {
        return getPassword().replaceAll(".","*");
    }

}
