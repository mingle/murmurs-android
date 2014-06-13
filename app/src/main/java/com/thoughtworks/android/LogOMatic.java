package com.thoughtworks.android;

import android.util.Log;

public class LogOMatic {

    public interface Logger {
        void debug(String msg);
        void info(String msg);
        void error(String msg);
        void error(String msg, Throwable t);
    }

    static class DefaultLogger implements Logger {

        private final String namespace;

        DefaultLogger(Class clazz) {
            namespace = clazz.getCanonicalName();
        }

        public void debug(String msg) {
            Log.d(namespace, msg);
        }

        public void info(String msg) {
            Log.i(namespace, msg);
        }

        public void error(String msg) {
            Log.e(namespace, msg);
        }

        public void error(String msg, Throwable t) {
            Log.e(namespace, msg, t);
        }
    }

    public static Logger getLogger(Class clazz) {
        return new DefaultLogger(clazz);
    }

}
