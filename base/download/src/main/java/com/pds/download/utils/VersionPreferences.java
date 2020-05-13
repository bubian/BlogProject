package com.pds.download.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pds.download.DownloadLibrary;

public class VersionPreferences {
    public static final String VERSION_CODE = "versionCode";
    public static final String TIPS_TIME = "tipsTime";

    public static SharedPreferences getSharedPreferences() {
        return DownloadLibrary.getContext().getSharedPreferences("version", Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(String key, boolean value) {
        return getSharedPreferences().getBoolean(key, value);
    }

    public static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key, long value) {
        return getSharedPreferences().getLong(key, value);
    }

    public static String getString(String key, String value) {
        return getSharedPreferences().getString(key, value);
    }

    public static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int value) {
        return getSharedPreferences().getInt(key, value);
    }

}
