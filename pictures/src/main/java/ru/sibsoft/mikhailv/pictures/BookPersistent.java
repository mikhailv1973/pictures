package ru.sibsoft.mikhailv.pictures;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by Mikhail on 2/17/2016.
 */
public class BookPersistent extends Book {
    private final SpiceManager spiceManager = new SpiceManager(LoadImageService.class);
    private Context context;
    private List<PagePersistent> pages;

    BookPersistent(Context context) {
        this.context = context.getApplicationContext();
    }
    @Override
    public void onStart() {
        spiceManager.start(context);

    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    @Override
    public Page getPage(int position) {
        return pages.get(position);
    }

    @Override
    protected void initializePages() {
        DataPictures dataPictures = new Gson().fromJson(new InputStreamReader(context.getResources().openRawResource(R.raw.pictures)), DataPictures.class);
        pages = new ArrayList<PagePersistent>();
        int number = 0;
        for(DataPicture item: dataPictures.pictures) {
            pages.add(new PagePersistent(item, number++));
        }

        try {
            DataFavourites dataFavourites = new Gson().fromJson(new InputStreamReader(context.openFileInput("favs")), DataFavourites.class);
            Map<String, DataFavourite> favs = new HashMap<String, DataFavourite>();
            for(DataFavourite fav: dataFavourites.favourites) {
                favs.put(fav.id, fav);
            }
            for(PagePersistent item: pages) {
                if(favs.containsKey(item.id)) {
                    item.addToFavourites(favs.get(item.id).text);
                }
            }
        } catch(FileNotFoundException e) {
        }
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return pages.get(position).createView((ViewPager)container);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        pages.get(position).destroyView((ViewPager) container, (View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        View view = (View)object;
        PagePersistent page = (PagePersistent)view.getTag(R.id.viewSource);
        if(page == null) {
            return PagerAdapter.POSITION_NONE;
        }
        page.updateFavourite(view);
        return page.getNumber();
    }

    private interface ImageRequestListener<RESULT> extends RequestListener<RESULT>, RequestProgressListener
    {
    }

    class PagePersistent implements Page {
        private String id;
        private String url;
        private Boolean favourite;
        private String note;
        private Integer number;

        PagePersistent(String id, String url, boolean favourite, String note, Integer number) {
            this.id = id;
            this.url = url;
            this.favourite = favourite;
            this.note = note;
            this.number = number;
        }

        PagePersistent(DataPicture dataPicture, Integer number) {
            this(dataPicture.id, dataPicture.url, false, "", number);
        }

        @Override
        public View createView(ViewPager container) {
            View view = ((Activity)container.getContext()).getLayoutInflater().inflate(R.layout.view_image, null);
            container.addView(view);
            view.setTag(R.id.viewSource, this);
            view.findViewById(R.id.imageView).setVisibility(View.GONE);
            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
            loadBitmap(view);
            updateFavourite(view);
            return view;
        }

        private void updateFavourite(View view) {
            if(favourite) {
                ((TextView) view.findViewById(R.id.textView)).setText(note);
                view.findViewById(R.id.textView).setVisibility(View.VISIBLE);
            } else {
                view.findViewById(R.id.textView).setVisibility(View.GONE);
            }
        }

        @Override
        public void destroyView(ViewPager container, View view) {
            view.setTag(R.id.viewSource, null);
            container.removeView(view);
        }

        @Override
        public int getNumber() {
            return number;
        }

        @Override
        public boolean addToFavourites(String note) {
            if(!this.favourite) {
                this.favourite = true;
                this.note = note == null ? "" : note;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean removeFromFavourites() {
            if(this.favourite) {
                this.favourite = false;
                this.note = "";
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public Page getRootPage() {
            return this;
        }

        @Override
        public boolean isFavourite() {
            return favourite;
        }

        private void loadBitmap(final View view) {
            spiceManager.execute(new SpiceRequest<Bitmap>(Bitmap.class) {
                @Override
                public Bitmap loadDataFromNetwork() throws Exception {
                    publishProgress(0F);
                    return BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
                }
            }, id, DurationInMillis.ALWAYS_RETURNED, new ImageRequestListener<Bitmap>() {
                @Override
                public void onRequestProgressUpdate(RequestProgress progress) {
                    if(view.getTag(R.id.viewSource) != null) {
                        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    if(view.getTag(R.id.viewSource) != null) {
                        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(null);
                        view.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
                @Override
                public void onRequestSuccess(Bitmap bitmap) {
                    if(view.getTag(R.id.viewSource) != null) {
                        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(bitmap);
                        view.findViewById(R.id.imageView).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }
            });
        }
    }

}
