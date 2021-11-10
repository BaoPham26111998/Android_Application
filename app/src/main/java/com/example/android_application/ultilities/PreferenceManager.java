package com.example.android_application.ultilities;

import android.content.Context;
import android.content.SharedPreferences;

//Defined and automatically generate UI interface for developer to set up values
public class PreferenceManager {

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(com.example.android_application.ultilities.Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    //Boolean preferences set up
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    //String preferences set up
    public void putString(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    //Remove preference setup
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
