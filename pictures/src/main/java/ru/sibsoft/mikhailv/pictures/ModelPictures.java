package ru.sibsoft.mikhailv.pictures;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.sibsoft.mikhailv.pictures.data.DataFavourite;
import ru.sibsoft.mikhailv.pictures.data.DataFavourites;
import ru.sibsoft.mikhailv.pictures.data.DataPicture;
import ru.sibsoft.mikhailv.pictures.data.DataPictures;

/**
 * Created by Mikhail on 2/7/2016.
 */
public class ModelPictures {

    private static final String LOG_TAG = ModelPictures.class.getSimpleName();

    private final SpiceManager spiceManager = new SpiceManager(LoadImageService.class);
    private final FragmentPictures fragment;
    private List<ListItem> listItems;
    private Preferences prefs = new Preferences();
    private Handler handlerSlideshow;

    public ModelPictures(FragmentPictures fragment) {
        this.fragment = fragment;
    }

    public void onStart() {
        spiceManager.start(fragment.getContext().getApplicationContext());

        DataPictures dataPictures = new Gson().fromJson(new InputStreamReader(fragment.getContext().getResources().openRawResource(R.raw.pictures)), DataPictures.class);
        listItems = new ArrayList<ListItem>();
        for(DataPicture item: dataPictures.pictures) {
            listItems.add(new ListItem(item));
        }

        try {
            DataFavourites dataFavourites = new Gson().fromJson(new InputStreamReader(fragment.getContext().openFileInput("favs")), DataFavourites.class);
            Map<String, DataFavourite> favs = new HashMap<String, DataFavourite>();
            for(DataFavourite fav: dataFavourites.favourites) {
                favs.put(fav.id, fav);
            }
            for(ListItem item: listItems) {
                if(favs.containsKey(item.id)) {
                    item.addToFavourites(favs.get(item.id).text);
                }
            }
        } catch(FileNotFoundException e) {
        }

        prefs = new Preferences(fragment.getActivity().getSharedPreferences("URA", Context.MODE_PRIVATE));
    }

    public void onResume() {
        prefs = new Preferences(fragment.getActivity().getSharedPreferences("URA", Context.MODE_PRIVATE));
        if(prefs.slideshow) {
            initSlideshow();
        } else {
            initManual();
        }
    }

    private void initManual() {
        ViewPager viewPager = fragment.findViewPager();
        if(viewPager != null) {
            PagerAdapter adapter = viewPager.getAdapter();
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initSlideshow() {
        if(handlerSlideshow == null) {
            handlerSlideshow = new Handler();
        }
        handlerSlideshow.removeCallbacksAndMessages(null);
        ViewPager viewPager = fragment.findViewPager();
        if(viewPager != null) {
            PagerAdapter adapter = viewPager.getAdapter();
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
        handlerSlideshow.post(new Runnable() {
            @Override
            public void run() {
                handlerSlideshow.postDelayed(this, prefs.interval * 1000);
                fragment.gotoPage(fragment.findViewPager().getCurrentItem() + 1, true);
            }
        });
    }

    public void onPause() {
        if(handlerSlideshow != null) {
            handlerSlideshow.removeCallbacksAndMessages(null);
            handlerSlideshow = null;
        }
    }

    public void onStop() {
        if(spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    public void closeRingIfNeeded(int position) {
        if(position != 0 && position != listItems.size() - 1) {
            return;
        }
        int copyTo = listItems.size() - 1 - position;
        for(int i = 0; i < 2; i++) {
            ListItem item = listItems.get(position);
            listItems.remove(position);
            listItems.add(copyTo, item);
        }
        fragment.findViewPager().getAdapter().notifyDataSetChanged();
    }

    public int getCount() {
        return prefs.slideshow ? Integer.MAX_VALUE : listItems.size();
    }

    public View createView(int position) {
        if(prefs.slideshow) {
            int index = prefs.showRandomly
                    ? (int)Math.floor(Math.random() * listItems.size())
                    : position % listItems.size();
            return new ListItem(listItems.get(index)).createView();
        } else {
            return listItems.get(position).createView();
        }
    }

    public void destroyView(int position, View view) {
        if(prefs.slideshow || position >= listItems.size()) {
            fragment.findViewPager().removeView(view);
        } else {
            listItems.get(position).destroyView();
        }
    }

    public void destroyAllViews() {
        for(ListItem item: listItems) {
            item.destroyView();
        }
    }

    public Integer getViewPosition(View view) {
        for(int i = 0; i < listItems.size(); i++) {
            if(listItems.get(i).view == view) {
                return i;
            }
        }
        return null;
    }

    public boolean canScroll() {
        return !prefs.slideshow;
    }

    private interface ImageRequestListener<RESULT> extends RequestListener<RESULT>, RequestProgressListener
    {
    }

    class ListItem {
        private String id;
        private String url;
        private Boolean favourite;
        private String note;
        private View view;

        ListItem(String id, String url, boolean favourite, String note) {
            this.id = id;
            this.url = url;
            this.favourite = favourite;
            this.note = note;
        }

        ListItem(DataPicture dataPicture) {
            this(dataPicture.id, dataPicture.url, false, "");
        }

        public ListItem(ListItem listItem) {
            this(listItem.id, listItem.url, listItem.favourite, listItem.note);
        }

        void addToFavourites(String note) {
            this.favourite = true;
            this.note = note == null ? "" : note;
        }

        void removeFromFavourites() {
            this.favourite = false;
            this.note = "";
        }

        View createView() {
            if(view == null) {
                view = fragment.getLayoutInflater(null).inflate(R.layout.view_image, null);
                fragment.findViewPager().addView(view);
                view.findViewById(R.id.imageView).setVisibility(View.GONE);
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                loadBitmap();
            }
            if(favourite) {
                ((TextView) view.findViewById(R.id.textView)).setText(note);
                view.findViewById(R.id.textView).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.textView).setVisibility(View.GONE);
            }
            return view;
        }

        public void destroyView() {
            if(view != null) {
                fragment.findViewPager().removeView(view);
                view = null;
            }
        }

        private void loadBitmap() {
            spiceManager.execute(new SpiceRequest<Bitmap>(Bitmap.class) {
                @Override
                public Bitmap loadDataFromNetwork() throws Exception {
                    publishProgress(0F);
                    return BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                }
            }, id, DurationInMillis.ALWAYS_RETURNED, new ImageRequestListener<Bitmap>() {
                @Override
                public void onRequestProgressUpdate(RequestProgress progress) {
                    if(view != null) {
                        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    if(view != null) {
                        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(null);
                        view.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
                @Override
                public void onRequestSuccess(Bitmap bitmap) {
                    if(view != null) {
                        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(bitmap);
                        view.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    class Preferences {
        Boolean slideshow = false;
        Integer interval = 1;
        public Boolean showFavoritesOnly = false;
        public Boolean showRandomly = false;

        Preferences(SharedPreferences prefs) {
            slideshow = prefs.getBoolean("slideshow", false);
            interval = prefs.getInt("interval", 1);
            showRandomly = prefs.getBoolean("showRandomly", false);
            showFavoritesOnly = prefs.getBoolean("showFavouritesOnly", false);
        }

        public Preferences() {
        }
    }

}
