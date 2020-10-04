package com.pds.storage.sp;

import android.content.Context;
import android.content.SharedPreferences;

class PreferencesHelper {

    private PreferencesHelper() {
        throw new AssertionError();
    }

    public static void putString(Context context, String fileName, String key, String value) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(Context context, String fileName, String key, String defaultValue) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    public static void putInt(Context context, String fileName, String key, int value) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public static int getInt(Context context, String fileName, String key, int defaultValue) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    public static void putLong(Context context, String fileName, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(Context context, String fileName, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    public static void putFloat(Context context, String fileName, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static float getFloat(Context context, String fileName, String key, float defaultValue) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    public static void putBoolean(Context context, String fileName, String key, boolean value) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String fileName, String key, boolean defaultValue) {
        SharedPreferences settings = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }

    public static void cleanData(Context context,String fileName) {
        SharedPreferences preferences = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void removeData(Context context, String fileName, String key) {
        SharedPreferences preferences = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void removeDataSync(Context context, String fileName, String key) {
        SharedPreferences preferences = context
                .getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }
}