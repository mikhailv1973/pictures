package ru.sibsoft.mikhailv.pictures;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

import ru.sibsoft.mikhailv.pictures.data.DataSettings;

/**
 * Created by Mikhail on 2/22/2016.
 */
public class Bookshelf extends Fragment {

    BookVirtual currentBook = new BookVirtual(new Book() {
        @Override
        void onStart() {
        }

        @Override
        public void onStop() {
        }

        @Override
        public Page getPage(int position) {
            return null;
        }

        @Override
        protected void initializePages() {
        }
    }) {
        @Override
        public Page getPage(int position) {
            return underlyingBook.getPage(position);
        }

        @Override
        protected void initializePages() {
        }

        @Override
        public int getCount() {
            return underlyingBook.getCount();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
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
