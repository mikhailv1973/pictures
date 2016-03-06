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
    private ViewPager viewPager;

    // region Lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        model.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewPager = (ViewPager)inflater.inflate(R.layout.fragment_pictures, container, false);
        viewPager.setAdapter(new PagerAdapter() {
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
                viewPager.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                model.destroyView(position);
                viewPager.removeView((View) object);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE) {
                    model.closeRingIfNeeded(viewPager.getCurrentItem());
                }
            }
        });
        model.closeRingIfNeeded(viewPager.getCurrentItem());
        return viewPager;
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
        viewPager.setCurrentItem(page, smoothScroll);
    }

}
