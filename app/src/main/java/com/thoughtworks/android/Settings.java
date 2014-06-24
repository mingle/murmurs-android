package com.thoughtworks.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.common.base.Joiner;

import java.util.Arrays;

public class Settings {

    private final SharedPreferences preferences;

    private Settings(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    public static Settings under(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new Settings(preferences);
    }

    public String getBaseUrl() {
        return preferences.getString("url", "");
    }

    public String getFallbackIconUrl(String initial) {
        return Joiner.on('/').join(getBaseUrl(), "images", "avatars", initial.toLowerCase() + ".png");
    };

    public String getEmail() {
        return preferences.getString("email", "");
    }

    public String getPassword() {
        return preferences.getString("password", "");
    }

    public String getProjectIdentifier() {
        return preferences.getString("project_identifier", "");
    }

    public String getMurmursRestResource() {
        return "murmurs.xml";
    }

    public String getMurmursIndexUrl() {
        return Joiner.on('/').join(Arrays.asList(getBaseUrl(), "api/v2/projects", getProjectIdentifier(), getMurmursRestResource()));
    }
}
