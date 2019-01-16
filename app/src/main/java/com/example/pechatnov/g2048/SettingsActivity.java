package com.example.pechatnov.g2048;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import android.util.Log;

public class SettingsActivity extends ActivityWithSettings {

    private SettingsKeeper settingsKeeper;
    SeekBar fieldSizeBar;
    TextView fieldSizeValue;

    SeekBar swipeSpeedBar;
    TextView swipeSpeedValue;

    RadioGroup radioGroup;
    RadioButton randomButton;
    RadioButton centerButton;

    SeekBar.OnSeekBarChangeListener onFieldSizeBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progress += 3;
//            if (progress < 3) {
//                progress = 3;
//                setProgress(3);
//            }
            String progressString = String.valueOf(progress);

            fieldSizeValue.setText(progressString);
            settingsKeeper.setFieldSize(progressString);

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_SIZE, progressString).apply();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO?
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO?
        }
    };

    public SeekBar.OnSeekBarChangeListener onSwipeSpeedChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            progress += 50;
//
//            if (progress < 50) {
//                progress = 50;
//                setProgress(50);
//            }
            String progressString = String.valueOf(progress);

            swipeSpeedValue.setText(progressString);
            settingsKeeper.setSwipeSpeed(progressString);
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_SPEED, progressString).apply();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO ?
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO ?
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.toolbar));
        super.onCreate(savedInstanceState);

        fieldSizeBar = findViewById(R.id.fieldSize);
        fieldSizeValue = findViewById(R.id.fieldSizeValue);
        swipeSpeedBar = findViewById(R.id.swipeSpeed);
        swipeSpeedValue = findViewById(R.id.swipeSpeedValue);
        radioGroup = findViewById(R.id.rgBlockStrategy);
        randomButton = findViewById(R.id.rbBlockRandom);
        centerButton = findViewById(R.id.rbBlockCenter);

//        Bundle bundle = getIntent().getExtras();

        settingsKeeper = new SettingsKeeper(this);
//        settingsKeeper.setFieldSize(bundle.get(PREF_SIZE).toString());
//        settingsKeeper.setSwipeSpeed(bundle.get(PREF_SPEED).toString());
//        settingsKeeper.setBlockStrategy(bundle.get(PREF_BLOCK).toString());

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SIZE, settingsKeeper.getFieldSize()).apply();
//        editor.putString(PREF_SIZE, bundle.get(PREF_SIZE).toString()).apply();
        editor.putString(PREF_SPEED, settingsKeeper.getSwipeSpeed()).apply();
        editor.putString(PREF_BLOCK, settingsKeeper.getBlockStrategyStr()).apply();

        fieldSizeBar.setOnSeekBarChangeListener(onFieldSizeBarChangeListener);
        swipeSpeedBar.setOnSeekBarChangeListener(onSwipeSpeedChangeListener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                switch (checkedId) {
                    case R.id.rbBlockCenter:
                        settingsKeeper.setBlockStrategy(getString(R.string.block_center_eng));
                        editor.putString(PREF_BLOCK, getString(R.string.block_center_eng)).apply();
                        break;
                    case R.id.rbBlockRandom:
                        settingsKeeper.setBlockStrategy(getString(R.string.block_random_eng));
                        editor.putString(PREF_BLOCK, getString(R.string.block_random_eng)).apply();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        settingsKeeper = new SettingsKeeper(this);
//        settingsKeeper.setFieldSize(sharedPreferences.getString(PREF_SIZE, "4"));
//        settingsKeeper.setSwipeSpeed(sharedPreferences.getString(PREF_SPEED, "500"));
//        settingsKeeper.setBlockStrategy(sharedPreferences.getString(PREF_BLOCK, String.valueOf(R.string.block_random_eng)));

        fieldSizeBar.setProgress(Integer.parseInt(settingsKeeper.getFieldSize()) - 3);
        fieldSizeValue.setText(settingsKeeper.getFieldSize());
        swipeSpeedBar.setProgress(Integer.parseInt(settingsKeeper.getSwipeSpeed()) - 50);
        swipeSpeedValue.setText(settingsKeeper.getSwipeSpeed());

        String blockStrategy = settingsKeeper.getBlockStrategyStr();
        if (blockStrategy.equals(getString(R.string.block_center_eng))) {
            centerButton.setChecked(true);
        }
        if (blockStrategy.equals(getString(R.string.block_random_eng))) {
            randomButton.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

//        intent.putExtra(SettingsActivity.PREF_SIZE, settingsKeeper.getFieldSize());
        intent.putExtra(SettingsActivity.PREF_SIZE, sharedPreferences.getString(PREF_SIZE, "4"));
        intent.putExtra(SettingsActivity.PREF_SPEED, settingsKeeper.getSwipeSpeed());
//        Toast.makeText(SettingsActivity.this, , Toast.LENGTH_SHORT).show();
        intent.putExtra(SettingsActivity.PREF_BLOCK, settingsKeeper.getBlockStrategyStr());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Toast.makeText(SettingsActivity.this, "Save instance state", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        outState.putString(SettingsActivity.PREF_SIZE, sharedPreferences.getString(PREF_SIZE, "4"));
        outState.putString(SettingsActivity.PREF_SPEED, sharedPreferences.getString(PREF_SPEED, "500"));
        outState.putString(SettingsActivity.PREF_BLOCK, sharedPreferences.getString(PREF_SIZE, "At random"));
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("requests","onDestroy in SettingsActivity");
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SIZE, settingsKeeper.getFieldSize()).apply();
        editor.putString(PREF_SPEED, settingsKeeper.getSwipeSpeed()).apply();
        editor.putString(PREF_BLOCK, settingsKeeper.getBlockStrategyStr()).apply();
    }

}
