package ru.sibsoft.mikhailv.pictures;

import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mikhail on 2/18/2016.
 */
public abstract class BookVirtual extends Book {
    protected Book underlyingBook = null;
    int countObservers = 0;
    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            initializePages();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            initializePages();
            notifyDataSetChanged();
        }
    };

    public BookVirtual(Book underlyingBook) {
        this.underlyingBook = underlyingBook;
    }

    public void setUnderlyingBook(Book book) {
        if(book == underlyingBook) {
            return;
        }
        if(underlyingBook != null && countObservers != 0) {
            underlyingBook.unregisterDataSetObserver(this.observer);
        }
        underlyingBook = book;
        if(countObservers != 0) {
            underlyingBook.registerDataSetObserver(this.observer);
            initializePages();
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
        countObservers++;
        if(countObservers == 1 && underlyingBook != null) {
            underlyingBook.registerDataSetObserver(this.observer);
            initializePages();
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        super.unregisterDataSetObserver(observer);
        countObservers--;
        if(countObservers == 0) {
            underlyingBook.unregisterDataSetObserver(this.observer);
        }
    }

    @Override
    public void onStart() {
        underlyingBook.onStart();
    }

    @Override
    public void onStop() {
        underlyingBook.onStop();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return getPage(position).createView((ViewPager)container);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        getPageFromObject(object, Page.class).destroyView((ViewPager)container, (View)object);
    }

    @Override
    public boolean contains(Page page) {
        try {
            PageVirtual pageVirtual = (PageVirtual)page;
            return underlyingBook.contains(pageVirtual.getUnderlyingPage());
        } catch(ClassCastException e) {
            return false;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        try {
            PageVirtual page = getPageFromObject(object, PageVirtual.class);
            return contains(page.getUnderlyingPage())
                    ? getPageFromObject(object, Page.class).getNumber()
                    : POSITION_NONE;
        } catch(ClassCastException e) {
            return POSITION_NONE;
        }
    }

}
