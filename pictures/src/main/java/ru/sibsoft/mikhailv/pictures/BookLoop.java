package ru.sibsoft.mikhailv.pictures;

import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mikhail on 2/18/2016.
 */
public class BookLoop extends BookVirtual {

    private List<PageVirtual> pages;
    private boolean needNotify = false;
    private int delta = 0;
    private int newPosition = 0;

    public BookLoop(Book book) {
        super(book);
    }

    @Override
    public int getCount() {
        int count = underlyingBook.getCount();
        return count > 1 ? count + 2 : count;
    }

    @Override
    protected void initializePages() {
        pages = new ArrayList<PageVirtual>();
        for(int i = 0, count = getCount(); i < count; i++) {
            pages.add(new PageVirtual(underlyingBook.getPage(i % underlyingBook.getCount()), i));
        }
    }

    @Override
    public Page getPage(int position) {
        return pages.get(position).WithNumber(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        delta = position == 0 ? getCount() - 2
                : position == getCount() - 1 ? 2 - getCount()
                : 0;
        newPosition = position + delta;
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        Set<Integer> onScreen = new HashSet<>();
        boolean needNotify = false;
        for(int i = 0; i < container.getChildCount(); i++) {
            try {
                Page page = getPageFromObject(container.getChildAt(i), Page.class);
                int newNumber = page.getNumber() + delta;
                int rootNumber = page.getRootPage().getNumber();
                if(0 <= newNumber && newNumber < getCount()) {
                    needNotify = needNotify || newNumber != page.getNumber();
                    page.setNumber(newNumber);
                    onScreen.add(rootNumber);
                } else if(onScreen.contains(rootNumber)) {
                    needNotify = needNotify || POSITION_NONE != page.getNumber();
                    page.setNumber(POSITION_NONE);
                } else {
                    needNotify = needNotify || rootNumber != page.getNumber();
                    onScreen.add(rootNumber);
                    page.setNumber(rootNumber);
                }
            } catch(ClassCastException e) {
            }
        }
        delta = 0;
        if(needNotify) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        try {
            PageVirtual page = getPageFromObject(object, PageVirtual.class);
            return underlyingBook.getCount() > 0 && (page.getNumber() % underlyingBook.getCount()) == page.getRootPage().getNumber()
                    ? page.getNumber()
                    : POSITION_NONE;
        } catch(ClassCastException e) {
            return POSITION_NONE;
        }
    }
}
