package com.example.pechatnov.g2048;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class ActivityWithSettings extends AppCompatActivity {

    protected Integer playScore = 0;

    public static String PREF_SIZE = "PREF_SIZE";
    public static String PREF_SPEED = "PREF_SPEED";
    public static String PREF_BLOCK = "PREF_BLOCK";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            case R.id.action_settings:
                // Переход на activity с настройками
                Intent startSettingsActivityIntent = new Intent(ActivityWithSettings.this, SettingsActivity.class);
                startActivityForResult(startSettingsActivityIntent, 1);
                break;

            case R.id.action_rating:
                // Переход на activity с рейтингом
                Intent startRatingActivityIntent = new Intent(ActivityWithSettings.this, RatingActivity.class);
                startRatingActivityIntent.putExtra("score", -1);
                startActivity(startRatingActivityIntent);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
