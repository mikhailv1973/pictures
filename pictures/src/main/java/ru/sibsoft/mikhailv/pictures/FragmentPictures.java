package ru.sibsoft.mikhailv.pictures;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Mikhail on 2/7/2016.
 */
public class FragmentPictures extends Fragment {

    private ModelPictures model = new ModelPictures(this);

    // region Lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        model.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pictures, container, false);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        findViewPager().setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return model.getCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return model.createView(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                model.destroyView(position, (View)object);
            }

            @Override
            public int getItemPosition(Object object) {
                Integer position = model.getViewPosition((View)object);
                return position == null ? POSITION_NONE : position;
            }
        });
        findViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE) {
                    model.closeRingIfNeeded(findViewPager().getCurrentItem());
                }
            }
        });
        findViewPager().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !model.canScroll();
            }
        });
        if(savedInstanceState == null) {
            gotoPage(1, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    public void onPause() {
        model.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        model.destroyAllViews();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        model.onStop();
        super.onDestroy();
    }

    // endregion

    public void gotoPage(int page, boolean smoothScroll) {
        findViewPager().setCurrentItem(page, smoothScroll);
    }

    ViewPager findViewPager() {
        return (ViewPager)getView().findViewById(R.id.view_pager);
    }
}
