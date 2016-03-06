package ru.sibsoft.mikhailv.pictures;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Mikhail on 2/18/2016.
 */
class PageVirtual implements Page {

    private final Page page;
    private Integer number;

    PageVirtual(Page page, Integer number) {
        this.page = page;
        this.number = number;
    }

    @Override
    public View createView(ViewPager container) {
        View view = page.createView(container);
        view.setTag(R.id.viewSource, this);
        return view;
    }

    @Override
    public void destroyView(ViewPager container, View view) {
        page.destroyView(container, view);
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public boolean addToFavourites(String note) {
        return page.addToFavourites(note);
    }

    @Override
    public boolean removeFromFavourites() {
        return page.removeFromFavourites();
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public Page getRootPage() {
        return page.getRootPage();
    }

    @Override
    public boolean isFavourite() {
        return getRootPage().isFavourite();
    }

    public PageVirtual WithNumber(int position) {
        return new PageVirtual(page, position);
    }
}
