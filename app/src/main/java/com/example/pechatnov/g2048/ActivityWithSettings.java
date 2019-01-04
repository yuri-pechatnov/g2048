package com.example.pechatnov.g2048;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class ActivityWithSettings extends AppCompatActivity {

    private SettingsKeeper settingsKeeper;

    public static String PREF_SIZE = "PREF_SIZE";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        settingsKeeper = new SettingsKeeper(this);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        settingsKeeper.setFieldSize(sharedPreferences.getString(PREF_SIZE, "4"));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Здесь нужно получить значение текущих настроек

            case R.id.action_settings:
                String fieldSize = settingsKeeper.getFieldSize();

                // Переход на activity с настройками
                Intent startSettingsActivityIntent = new Intent(ActivityWithSettings.this, SettingsActivity.class);
                startSettingsActivityIntent.putExtra(SettingsActivity.PREF_SIZE, fieldSize);
                startActivity(startSettingsActivityIntent);
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
}
