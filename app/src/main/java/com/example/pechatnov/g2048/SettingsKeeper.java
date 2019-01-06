package com.example.pechatnov.g2048;

import java.lang.reflect.Type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SettingsKeeper {

    private static final String SHARED_PREF_NAME = "SHARED_PREF_NAME";
    private static final String FIELD_SIZE_KEY = "FIELD_SIZE_KEY";
    private static final Type FIELD_SIZE_TYPE = new TypeToken<String>() {}.getType();

    private static final String SWIPE_SPEED_KEY = "SWIPE_SPEED_KEY";
    private static final Type SWIPE_SPEED_TYPE = new TypeToken<String>() {}.getType();

    private static final String BLOCK_STRATEGY_KEY = "BLOCK_STRATEGY_KEY";
    private static final Type BLOCK_STRATEGY_TYPE = new TypeToken<String>() {}.getType();

    private Gson mGson = new Gson();
    private SharedPreferences sharedPreferences;

    SettingsKeeper(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getFieldSize() {
        return mGson.fromJson(sharedPreferences.getString(FIELD_SIZE_KEY, "4"), FIELD_SIZE_TYPE);
    }
    public void setFieldSize(String fieldSize) {
        sharedPreferences.edit().putString(FIELD_SIZE_KEY, mGson.toJson(fieldSize, FIELD_SIZE_TYPE)).apply();
    }

    public String getSwipeSpeed() {
        return mGson.fromJson(sharedPreferences.getString(SWIPE_SPEED_KEY, "500"), SWIPE_SPEED_TYPE);
    }
    public  void setSwipeSpeed(String swipeSpeed) {
        sharedPreferences.edit().putString(SWIPE_SPEED_KEY, mGson.toJson(swipeSpeed, SWIPE_SPEED_TYPE)).apply();
    }

    public String getBlockStrategy() {
        return mGson.fromJson(sharedPreferences.getString(BLOCK_STRATEGY_KEY, String.valueOf(R.string.block_random)), BLOCK_STRATEGY_TYPE);
    }
    @SuppressLint("Assert")
    public  void setBlockStrategy(String blockStrategy) {
        assert (blockStrategy.equals("Случайно") || blockStrategy.equals("По центру"));
        sharedPreferences.edit().putString(BLOCK_STRATEGY_KEY, mGson.toJson(blockStrategy, BLOCK_STRATEGY_TYPE)).apply();
    }

}
