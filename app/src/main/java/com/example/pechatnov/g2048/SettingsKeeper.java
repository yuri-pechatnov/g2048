package com.example.pechatnov.g2048;

import java.lang.reflect.Type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Map;
import java.util.HashMap;

public class SettingsKeeper {

    public enum BlockStrategy {
        CENTER(0),
        RANDOM(1),
        RANDOM_CORNER(2);

        private static final Map<Integer, BlockStrategy> typesByValue = new HashMap<Integer, BlockStrategy>();

        static {
            for (BlockStrategy type : BlockStrategy.values()) {
                typesByValue.put(type.value, type);
            }
        }

        final Integer value;

        private BlockStrategy(int value) {
            this.value = value;
        }

        public static BlockStrategy fromInt(Integer value) {
            return typesByValue.get(value);
        }
    }

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

    public BlockStrategy getBlockStrategy() {
        String strategyStr = getBlockStrategyStr();
        if (strategyStr != null && strategyStr.equals(String.valueOf(R.string.block_random_eng))) {
            return BlockStrategy.RANDOM;
        } else {
            return BlockStrategy.CENTER;
        }
    }

    public String getBlockStrategyStr() {
        return mGson.fromJson(sharedPreferences.getString(BLOCK_STRATEGY_KEY, String.valueOf(R.string.block_random_eng)), BLOCK_STRATEGY_TYPE);
    }
    @SuppressLint("Assert")
    public  void setBlockStrategy(String blockStrategy) {
        assert (blockStrategy.equals("At random") || blockStrategy.equals("In the center"));
        sharedPreferences.edit().putString(BLOCK_STRATEGY_KEY, mGson.toJson(blockStrategy, BLOCK_STRATEGY_TYPE)).apply();
    }

}
