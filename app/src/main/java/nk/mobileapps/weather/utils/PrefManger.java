package nk.mobileapps.weather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by nagendra on 22/12/16.
 */
public class PrefManger {


    public static void putSharedPreferencesInt(Context context, String key,
                                               int value) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putInt(key, value);
        edit.commit();
    }

    public static void putSharedPreferencesBoolean(Context context, String key,
                                                   boolean val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putBoolean(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesString(Context context, String key,
                                                  String val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putString(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesFloat(Context context, String key,
                                                 float val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putFloat(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesLong(Context context, String key,
                                                long val) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.putLong(key, val);
        edit.commit();
    }

    public static void putSharedPreferencesStringArray(Context context,
                                                       String key, String[] val, String separator) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        Editor edit = preferences.edit();

        String value = new String();

        for (int i = 0; i < val.length; i++) {
            value = value + val[i] + separator;
        }
        System.out.println("Feb4 Value is: " + value);
        edit.putString(key, value);
        edit.commit();

    }

    public static String[] getSharedPreferencesStringArray(Context context,
                                                           String key, String _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String value = preferences.getString(key, _default);
        String separator = null;


        if (value != _default) {


            separator = value.substring(value.length() - 1);


            value = value.substring(0, value.length() - 1);


            String result[] = value.trim().split(separator.trim());


            return result;
        }

        return null;
    }

    public static long getSharedPreferencesLong(Context context, String key,
                                                long _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getLong(key, _default);
    }

    public static float getSharedPreferencesFloat(Context context, String key,
                                                  float _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getFloat(key, _default);
    }

    public static String getSharedPreferencesString(Context context,
                                                    String key, String _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getString(key, _default);
    }

    public static int getSharedPreferencesInt(Context context, String key,
                                              int _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getInt(key, _default);
    }

    public static boolean getSharedPreferencesBoolean(Context context,
                                                      String key, boolean _default) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, _default);
    }


    public static boolean checkIsKeyExist(Context c, String key) {

        if (getSharedPreferencesString(c, key, null) == null
                || getSharedPreferencesString(c, key, null).equals(""))
            return false;

        return true;
    }

    public static void clearSharedPreferences(Context context) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor edit = preferences.edit();
        edit.clear();
        edit.commit();

    }

    public static void printData(Activity a) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(a);
        HashMap hm = (HashMap) sp.getAll();
        Set s = hm.keySet();
        Iterator nav = s.iterator();

        while (nav.hasNext()) {
            String key = (String) nav.next();
            System.out.println("Key: " + key + " -- Value: "
                    + sp.getString(key, " (EMPTY) "));
        }

    }


}