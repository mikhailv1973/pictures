package ru.sibsoft.mikhailv.pictures;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
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
                View view = model.createView(position);
                findViewPager().addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                model.destroyView(position);
                findViewPager().removeView((View) object);
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
        return (ViewPager)getView();
    }
}
