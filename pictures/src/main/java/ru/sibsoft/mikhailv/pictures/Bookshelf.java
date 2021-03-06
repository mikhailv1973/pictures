package ru.sibsoft.mikhailv.pictures;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import ru.sibsoft.mikhailv.pictures.data.DataSettings;

/**
 * Created by Mikhail on 2/22/2016.
 */
public class Bookshelf extends Fragment {

    private static final String LOG_TAG = Bookshelf.class.getSimpleName();

    BookVirtual currentBook = new BookVirtual(Book.empty) {
        @Override
        public Page getPage(int position) {
            Log.d(LOG_TAG, String.format("getPage: position: %d", position));
            return underlyingBook.getPage(position);
        }

        @Override
        protected void initializePages() {
            Log.d(LOG_TAG, String.format("initializePages"));
        }

        @Override
        public int getCount() {
            return underlyingBook.getCount();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if(object == null) {
                Log.d(LOG_TAG, String.format("setPrimaryItem: position: %d, object: nunll", position));
            } else {
                Log.d(LOG_TAG, String.format("setPrimaryItem: position: %d, number: %d, rootNumber: %d",
                        position, getPageFromObject(object, PageVirtual.class).getNumber(),
                        getPageFromObject(object, PageVirtual.class).getRootPage().getNumber()));
            }
            underlyingBook.setPrimaryItem(container, position, object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            underlyingBook.finishUpdate(container);
        }

        @Override
        public void startUpdate(ViewGroup container) {
            underlyingBook.startUpdate(container);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            Log.d(LOG_TAG, String.format("instantiateItem: position: %d, number: %d, rootNumber: %d",
                    position, getPageFromObject(object, PageVirtual.class).getNumber(),
                    getPageFromObject(object, PageVirtual.class).getRootPage().getNumber()));
            return object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.d(LOG_TAG, String.format("destroyItem: position: %d, number: %d, rootNumber: %d",
                    position, getPageFromObject(object, PageVirtual.class).getNumber(),
                    getPageFromObject(object, PageVirtual.class).getRootPage().getNumber()));
            super.destroyItem(container, position, object);
        }

        @Override
        public int getItemPosition(Object object) {
            int position = super.getItemPosition(object);
            Log.d(LOG_TAG, String.format("getItemPosition: position: %d, number: %d, rootNumber: %d",
                    position, getPageFromObject(object, PageVirtual.class).getNumber(),
                    getPageFromObject(object, PageVirtual.class).getRootPage().getNumber()));
            return position;
        }
    };
    private BookPersistent persistent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        persistent = new BookPersistent(getContext());
        persistent.initializePages();
        persistent.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        currentBook.onStop();
        currentBook = null;
        super.onDestroy();
    }

    public void rebuild() {
        DataSettings settings = new DataSettings(getContext().getSharedPreferences("URA", Context.MODE_PRIVATE));
        Book favsFilter = settings.showFavoritesOnly
                ? new BookFilterFavourites(persistent)
                : persistent;
        BookVirtual underlyingBook = settings.slideshow
                ? new BookSlideshow(favsFilter, settings.showRandomly)
                : new BookLoop(favsFilter);
        currentBook.setUnderlyingBook(underlyingBook);
        persistent.notifyDataSetChanged();
    }

    public PagerAdapter getAdapter() {
        return currentBook;
    }

    public boolean isSlideshow() {
        return new DataSettings(getContext().getSharedPreferences("URA", Context.MODE_PRIVATE)).slideshow;
    }

    public long getSlideshowDelay() {
        return new DataSettings(getContext().getSharedPreferences("URA", Context.MODE_PRIVATE)).interval;
    }
}
