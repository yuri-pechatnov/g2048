package com.example.pechatnov.g2048;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

public class SettingsActivity extends ActivityWithSettings {

    private SettingsKeeper settingsKeeper;
    SeekBar fieldSizeBar;

    // TODO: добавить seekbar listener

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.settings_activity);
        setSupportActionBar((android.support.v7.widget.Toolbar)findViewById(R.id.toolbar));

        fieldSizeBar = findViewById(R.id.fieldSize);

        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        settingsKeeper = new SettingsKeeper(this);
        settingsKeeper.setFieldSize(bundle.get(PREF_SIZE).toString());

        fieldSizeBar.setMin(3);  // TODO: тут какие-то проблемы
        fieldSizeBar.setMax(8);
        fieldSizeBar.setProgress(Integer.parseInt(settingsKeeper.getFieldSize()));
    }
}
