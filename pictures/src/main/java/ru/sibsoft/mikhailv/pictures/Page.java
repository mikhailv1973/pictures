package ru.sibsoft.mikhailv.pictures;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Mikhail on 2/18/2016.
 */
public interface Page {

    View createView(ViewPager container);

    void destroyView(ViewPager container, View view);

    int getNumber();

    boolean addToFavourites(String note);

    boolean removeFromFavourites();

    void setNumber(int number);

    Page getRootPage();

    boolean isFavourite();
}
