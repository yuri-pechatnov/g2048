package com.example.pechatnov.g2048;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class ActivityWithSettings extends AppCompatActivity {

    protected SettingsKeeper settingsKeeper;

    public static String PREF_SIZE = "PREF_SIZE";
    public static String PREF_SPEED = "PREF_SPEED";
    public static String PREF_BLOCK = "PREF_BLOCK";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(ActivityWithSettings.this, "Create ActivityWithSettings", Toast.LENGTH_SHORT).show();

        settingsKeeper = new SettingsKeeper(this);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        settingsKeeper.setFieldSize(sharedPreferences.getString(PREF_SIZE, "4"));
        settingsKeeper.setSwipeSpeed(sharedPreferences.getString(PREF_SPEED, "500"));
        settingsKeeper.setBlockStrategy(sharedPreferences.getString(PREF_BLOCK, getString(R.string.block_random)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Здесь нужно получить значение текущих настроек

            case R.id.action_settings:
                String fieldSize = settingsKeeper.getFieldSize();
                String swipeSpeed = settingsKeeper.getSwipeSpeed();
                String blockStrategy = settingsKeeper.getBlockStrategy();

                // Переход на activity с настройками
                Intent startSettingsActivityIntent = new Intent(ActivityWithSettings.this, SettingsActivity.class);
                startSettingsActivityIntent.putExtra(SettingsActivity.PREF_SIZE, fieldSize);
                startSettingsActivityIntent.putExtra(SettingsActivity.PREF_SPEED, swipeSpeed);
                startSettingsActivityIntent.putExtra(SettingsActivity.PREF_BLOCK, blockStrategy);
                startActivityForResult(startSettingsActivityIntent, 1);
                break;

//            case R.id.actionSearch:
//                Toast.makeText(SingleFragmentActivity.this, R.string.menuSearch, Toast.LENGTH_SHORT).show();
//
//                getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragmentContainer, SearchFragment.newInstance())
//                        .addToBackStack(SearchFragment.class.getName())
//                        .commit();
//
//
//                break;
//            case R.id.actionExit:
//                Toast.makeText(SingleFragmentActivity.this, R.string.menuExit, Toast.LENGTH_SHORT).show();
//                finish();
//                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                settingsKeeper.setFieldSize(data.getStringExtra(ActivityWithSettings.PREF_SIZE));
                settingsKeeper.setSwipeSpeed(data.getStringExtra(ActivityWithSettings.PREF_SPEED));
                settingsKeeper.setBlockStrategy(data.getStringExtra(ActivityWithSettings.PREF_BLOCK));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_SIZE, settingsKeeper.getFieldSize()).apply();
        editor.putString(PREF_SPEED, settingsKeeper.getSwipeSpeed()).apply();
        editor.putString(PREF_BLOCK, settingsKeeper.getBlockStrategy()).apply();
    }

}
