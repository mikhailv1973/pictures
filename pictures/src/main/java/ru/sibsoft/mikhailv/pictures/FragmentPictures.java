package ru.sibsoft.mikhailv.pictures;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mikhail on 2/7/2016.
 */
public class FragmentPictures extends Fragment {

    private Handler slideshowHandler;

    // region Lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pictures, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewPager viewPager = (ViewPager)getView().findViewById(R.id.view_pager);
        viewPager.setAdapter(getBookshelf().getAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isSlideshow();
            }
        });
    }

    private Bookshelf getBookshelf() {
        return ((ActivityPictures)getActivity()).getBookshelf();
    }

    @Override
    public void onResume() {
        super.onResume();
        getBookshelf().rebuild();
        getBookshelf().getAdapter().finishUpdate(findViewPager());
        if(isSlideshow()) {
            final Long delay = getSlideshowDelay() * 1000;
            slideshowHandler = new Handler();
            slideshowHandler.post(new Runnable() {
                @Override
                public void run() {
                    slideshowHandler.removeCallbacks(this);
                    findViewPager().setCurrentItem(findViewPager().getCurrentItem() + 1, true);
                    slideshowHandler.postDelayed(this, delay);
                }
            });
        }
    }

    private Long getSlideshowDelay() {
        return getBookshelf().getSlideshowDelay();
    }

    @Override
    public void onPause() {
        if(slideshowHandler != null) {
            slideshowHandler.removeCallbacksAndMessages(null);
            slideshowHandler = null;
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        findViewPager().setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // endregion

    public void gotoPage(int page, boolean smoothScroll) {
        findViewPager().setCurrentItem(page, smoothScroll);
    }

    ViewPager findViewPager() {
        return (ViewPager)getView().findViewById(R.id.view_pager);
    }

    public boolean isVisiblePageFavourite() {
        return false;
    }

    public boolean isSlideshow() {
        return getBookshelf().isSlideshow();
    }
}
