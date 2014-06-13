package com.thoughtworks.android;

import com.google.common.base.Joiner;
import com.thoughtworks.android.RuntimeMode;

import org.apache.commons.logging.Log;

import java.util.Arrays;
import java.util.logging.Level;

public class Settings {

    public static RuntimeMode getRuntimeMode() {
        return RuntimeMode.DEBUG;
    }

    public static boolean isAppInDebugMode() {
       return getRuntimeMode() == RuntimeMode.DEBUG;
    }

    public static String getBaseUrl() {
        return "http://192.168.1.65:3000/api/v2";
    }

    public static String getProjectIdentifier() {
        return "murmurs";
    }

    public static String getMurmursRestResource() {
        return "murmurs.xml";
    }

    public static String getMurmursIndexUrl() {
        return Joiner.on('/').join(Arrays.asList(getBaseUrl(), "projects", getProjectIdentifier(), getMurmursRestResource()));
    }
}
