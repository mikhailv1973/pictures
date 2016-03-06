package ru.sibsoft.mikhailv.pictures.data;

import android.content.SharedPreferences;

/**
 * Created by Mikhail on 2/7/2016.
 */
public class DataSettings {
    public Boolean slideshow;
    public Integer interval;
    public Boolean showFavoritesOnly;
    public Boolean showRandomly;
    public Integer transformer;

    public DataSettings(SharedPreferences preferences) {
        slideshow = preferences.getBoolean("slideshow", false);
        interval = preferences.getInt("interval", 1);
        showFavoritesOnly = preferences.getBoolean("showFavouritesOnly", false);
        showRandomly = preferences.getBoolean("showRandomly", false);
        transformer = preferences.getInt("transformer", -1);
    }
}
