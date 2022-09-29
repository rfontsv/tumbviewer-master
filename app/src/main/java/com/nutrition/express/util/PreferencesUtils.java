package com.nutrition.express.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nutrition.express.application.ExpressApplication;

/**
 * Created by huang on 9/23/16.
 */

public class PreferencesUtils {
    public static void putString(String key, String value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        return prefs.getString(key, "");
    }

    public static void putInt(String key, int value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        return prefs.getInt(key, 0);
    }

    public static void putBoolean(String key, boolean value) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, boolean val) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(ExpressApplication.getApplication());
        return prefs.getBoolean(key, val);
    }

}
