package com.protection.plpt.plpt.mpkz.mpkz.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtils {

	public static final String SHARED_NAME = "backup_app_shared";
	
	public static String getFromShared(final Context context, final String key) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		if (prefs.contains(key)) { return prefs.getString(key, ""); }
		return null;
	}

    public static boolean getBooleanFromShared(final Context context, final String key) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        if (prefs.contains(key)) { return prefs.getBoolean(key, false); }
        return false;
    }
	
	public static void writeToShared(final Context context,final String key, final String value) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if (prefs.contains(key)) { editor.remove(key); }
		editor.putString(key, value).commit();
	}

    public static void writeBooleanToShared(final Context context,final String key, final boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key)) { editor.remove(key); }
        editor.putBoolean(key, value).commit();
    }
	
	public static void deleteFromShared(final Context context, final String key) {
		SharedPreferences prefs = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		if (prefs.contains(key)) {
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove(key);
			editor.commit();
		}
	}
}
