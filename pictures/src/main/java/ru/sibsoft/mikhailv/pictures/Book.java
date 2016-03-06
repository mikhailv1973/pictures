package ru.sibsoft.mikhailv.pictures;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by Mikhail on 2/17/2016.
 */
public abstract class Book extends PagerAdapter {

    abstract void onStart();

    public abstract void onStop();

    public abstract Page getPage(int position);

    protected abstract void initializePages();

    public abstract boolean contains(Page page);

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    protected <P extends Page> P getPageFromObject(Object object, Class<P> clazz) throws ClassCastException {
        View view = (View)object;
        return view == null ? null : (P)view.getTag(R.id.viewSource);
    }

    @Override
    public Parcelable saveState() {
        return super.saveState();
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        super.restoreState(state, loader);
    }

    public static final Book empty = new Book() {
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

        @Override
        public boolean contains(Page page) {
            return false;
        }
    };
}
