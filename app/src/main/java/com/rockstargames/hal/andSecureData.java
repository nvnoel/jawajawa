package com.rockstargames.hal;

import android.content.Context;
import android.content.SharedPreferences;

public class andSecureData {
    private static SharedPreferences settings = null;
    private static SharedPreferences.Editor prefEditor = null;

    public static void Init(Context context) {
        settings = context.getSharedPreferences("prefs", 0);
        prefEditor = settings.edit();
    }

    public static String GetString(String key) {
        return settings.getString(key, "");
    }

    public static void SetString(String key, String value) {
        prefEditor.putString(key, value);
    }

    public static int GetInt(String key) {
        return settings.getInt(key, -1);
    }

    public static void SetInt(String key, int value) {
        prefEditor.putInt(key, value);
    }

    public static boolean GetBool(String key) {
        return settings.getBoolean(key, false);
    }

    public static void SetBool(String key, boolean value) {
        prefEditor.putBoolean(key, value);
    }

    public static float GetFloat(String key) {
        return settings.getFloat(key, -1.0f);
    }

    public static void SetFloat(String key, float value) {
        prefEditor.putFloat(key, value);
    }

    public static void Save() {
        prefEditor.commit();
    }
}