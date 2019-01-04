package com.example.pechatnov.g2048;

import java.lang.reflect.Type;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SettingsKeeper {

    public static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    public static final String FIELD_SIZE_KEY = "FIELD_SIZE_KEY";
    public static final Type FIELD_SIZE_TYPE = new TypeToken<String>() {}.getType();

    public static final String SWIPE_SPEED_KEY = "SWIPE_SPEED_KEY";
    public static final Type SWIPE_SPEED_TYPE = new TypeToken<String>() {}.getType();

    public static final String BLOCK_STRATEGY_KEY = "BLOCK_STRATEGY_KEY";
    public static final Type BLOCK_STRATEGY_TYPE = new TypeToken<String>() {}.getType();

    private Gson mGson = new Gson();
    private SharedPreferences sharedPreferences;

    public SettingsKeeper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getFieldSize() {
        return mGson.fromJson(sharedPreferences.getString(FIELD_SIZE_KEY, "4"), FIELD_SIZE_TYPE);
    }
    public void setFieldSize(String fieldSize) {
        sharedPreferences.edit().putString(FIELD_SIZE_KEY, mGson.toJson(fieldSize, FIELD_SIZE_TYPE)).apply();
    }

    // todo: swipe_speed, block_strategy

}
