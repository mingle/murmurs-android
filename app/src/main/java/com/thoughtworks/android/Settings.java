package com.thoughtworks.android;

import com.google.common.base.Joiner;

import java.util.Arrays;

public class Settings {

    public static String getBaseUrl() {
        return "https://murmurs-android-test.mingle-staging.thoughtworks.com";
    }

    public static String getFallbackIconUrl(String initial) {
        return Joiner.on('/').join(getBaseUrl(), "images", "avatars", initial.toLowerCase() + ".png");
    };

    public static String getProjectIdentifier() {
        return "test";
    }

    public static String getMurmursRestResource() {
        return "murmurs.xml";
    }

    public static String getMurmursIndexUrl() {
        return Joiner.on('/').join(Arrays.asList(getBaseUrl(), "api/v2/projects", getProjectIdentifier(), getMurmursRestResource()));
    }
}
