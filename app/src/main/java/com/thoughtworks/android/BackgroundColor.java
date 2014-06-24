package com.thoughtworks.android;

import android.graphics.Color;

import com.dephillipsdesign.logomatic.LogOMatic;
import com.dephillipsdesign.logomatic.Logger;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ported from the ruby in mingle/app/models/color.rb
 */
public class BackgroundColor {

    private static final Logger log = LogOMatic.getLogger(BackgroundColor.class);

    public static final String BLUE_GREEN = "#3D8F84";
    public static final String GREEN = "#19A657";
    public static final String LIME = "#55EB7D";
    public static final String TEAL = "#198FA6";
    public static final String DARK_TURQUIOSE = "#24C2CC";
    public static final String LIGHT_TURQUOISE = "#30E4EF";
    public static final String PURPLE = "#712468";
    public static final String HOT_PINK = "#EE5AA2";
    public static final String PASTEL_PINK = "#FFA5D1";
    public static final String RED = "#D4292B";
    public static final String RED_ORANGE = "#EE675A";
    public static final String ORANGE = "#EB9955";
    public static final String ORANGE_YELLOW = "#EBC855";
    public static final String YELLOW = "#EAEB55";
    public static final String BLACK = "#000000";

    private static final String[] COLORS = new String[] {BLUE_GREEN,
                                                         GREEN,
                                                         LIME,
                                                         TEAL,
                                                         DARK_TURQUIOSE,
                                                         LIGHT_TURQUOISE,
                                                         PURPLE,
                                                         HOT_PINK,
                                                         PASTEL_PINK,
                                                         RED,
                                                         RED_ORANGE,
                                                         ORANGE,
                                                         ORANGE_YELLOW,
                                                         YELLOW,
                                                         BLACK
                                                        };

    private static int hash(String string) {
        int hash = 0;

        for (int i=0; i < string.length(); i++) {
            int temp = (hash << 5) - hash + Character.getNumericValue(string.charAt(i));
            hash = temp & temp;
        }

        return hash;
    }

    public static String forUser(String username) {
        final int hash = Math.abs(hash(username));
        String css = COLORS[hash % COLORS.length];
        log.debugf("%s hashed to %d which is %s", username, hash, css);
        return css;
    }
}
