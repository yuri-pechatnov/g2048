package com.example.pechatnov.g2048;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
            String progressString = String.valueOf(progress);

            fieldSizeValue.setText(progressString);
            settingsKeeper.setFieldSize(progressString);
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
            String progressString = String.valueOf(progress);

            swipeSpeedValue.setText(progressString);
            settingsKeeper.setSwipeSpeed(progressString);
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
        setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));
        super.onCreate(savedInstanceState);

        fieldSizeBar = findViewById(R.id.fieldSize);
        fieldSizeValue = findViewById(R.id.fieldSizeValue);
        swipeSpeedBar = findViewById(R.id.swipeSpeed);
        swipeSpeedValue = findViewById(R.id.swipeSpeedValue);
        radioGroup = findViewById(R.id.rgBlockStrategy);
        randomButton = findViewById(R.id.rbBlockRandom);
        centerButton = findViewById(R.id.rbBlockCenter);

        Bundle bundle = getIntent().getExtras();

        settingsKeeper = new SettingsKeeper(this);
        settingsKeeper.setFieldSize(bundle.get(PREF_SIZE).toString());
        settingsKeeper.setSwipeSpeed(bundle.get(PREF_SPEED).toString());
        settingsKeeper.setBlockStrategy(bundle.get(PREF_BLOCK).toString());

//        fieldSizeBar.setMin(3);  // TODO: тут какие-то проблемы, не получается ткнуть дальше, чем на 5
//        fieldSizeBar.setMax(8);
        fieldSizeBar.setProgress(Integer.parseInt(settingsKeeper.getFieldSize()));
        fieldSizeValue.setText(settingsKeeper.getFieldSize());

        fieldSizeBar.setOnSeekBarChangeListener(onFieldSizeBarChangeListener);

//        swipeSpeedBar.setMin(50);  // TODO: тут тоже по непонятным мне причинам до конца не дотягивает, надо разбираться
//        swipeSpeedBar.setMax(1000);
        swipeSpeedBar.setProgress(Integer.parseInt(settingsKeeper.getSwipeSpeed()));
        swipeSpeedValue.setText(settingsKeeper.getSwipeSpeed());

        swipeSpeedBar.setOnSeekBarChangeListener(onSwipeSpeedChangeListener);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbBlockCenter:
                        settingsKeeper.setBlockStrategy(getString(R.string.block_center));
                        break;
                    case R.id.rbBlockRandom:
                        settingsKeeper.setBlockStrategy(getString(R.string.block_random));
                        break;
                }
            }
        });
        String blockStrategy = settingsKeeper.getBlockStrategyStr();
        if (blockStrategy.equals(getString(R.string.block_center))) {
            centerButton.setChecked(true);
        }
        if (blockStrategy.equals(getString(R.string.block_random))) {
            randomButton.setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(SettingsActivity.PREF_SIZE, settingsKeeper.getFieldSize());
        intent.putExtra(SettingsActivity.PREF_SPEED, settingsKeeper.getSwipeSpeed());
        intent.putExtra(SettingsActivity.PREF_BLOCK, settingsKeeper.getBlockStrategy());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

//    @Override
//    protected void onDestroy() {
//        Toast.makeText(SettingsActivity.this, "Destroy activity with settings", Toast.LENGTH_SHORT).show();
//        super.onDestroy();
//
//    }
}
