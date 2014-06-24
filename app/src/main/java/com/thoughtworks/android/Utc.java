package com.thoughtworks.android;

import java.util.Date;
import java.util.TimeZone;

public class Utc {

    public static Date toLocalTime(Date utc) {
        long offset = TimeZone.getDefault().getOffset(utc.getTime());
        long correctedTime = utc.getTime() + offset;
        return new Date(correctedTime);
    }
}
