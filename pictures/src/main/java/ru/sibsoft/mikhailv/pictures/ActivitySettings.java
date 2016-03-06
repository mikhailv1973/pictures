package ru.sibsoft.mikhailv.pictures;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.EditText;

public class ActivitySettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(savedInstanceState == null) {
            SharedPreferences prefs = getSharedPreferences("URA", MODE_PRIVATE);
            ((SwitchCompat)findViewById(R.id.slideshow)).setChecked(prefs.getBoolean("slideshow", false));
            ((EditText)findViewById(R.id.slideshow_interval)).setText(String.valueOf(prefs.getInt("interval", 1)));
            ((SwitchCompat)findViewById(R.id.random)).setChecked(prefs.getBoolean("showRandomly", false));
            ((SwitchCompat)findViewById(R.id.favourites)).setChecked(prefs.getBoolean("showFavouritesOnly", false));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("URA", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("slideshow", ((SwitchCompat) findViewById(R.id.slideshow)).isChecked());
        editor.putInt("interval", Integer.valueOf(((EditText) findViewById(R.id.slideshow_interval)).getText().toString()));
        editor.putBoolean("showRandomly", ((SwitchCompat) findViewById(R.id.random)).isChecked());
        editor.putBoolean("showFavouritesOnly", ((SwitchCompat)findViewById(R.id.favourites)).isChecked());
        editor.apply();
    }
}
