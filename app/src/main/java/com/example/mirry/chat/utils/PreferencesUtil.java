package com.example.mirry.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by Mirry on 2017/3/15.
 */

public class PreferencesUtil {
    private static final String PREF_NAME = "config";

    public static boolean getBoolean(Context context, String name, String key, boolean defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }
    public static void setBoolean(Context context, String name,String key,boolean value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String getString(Context context, String name,String key, String defaultValue){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
    public static void setString(Context context,String name,String key,String value){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Map<String,String> getAll(Context context,String name){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        Map<String, String> all = (Map<String, String>) sp.getAll();
        return all;
    }

    public static void deleteItem(Context context,String name ,String key){
        SharedPreferences sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

}
