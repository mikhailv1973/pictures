package ru.sibsoft.mikhailv.pictures;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private final SpiceManager spiceManager = new SpiceManager(LoadImageService.class);
    private final FragmentPictures fragment;
    private List<ListItem> listItems;

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
        if(!listItems.isEmpty()) {
            listItems.add(0, new ListItem(dataPictures.pictures.get(dataPictures.pictures.size() - 1)));
            listItems.add(new ListItem(dataPictures.pictures.get(0)));
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
    }

    public void onStop() {
        if(spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    public void closeRingIfNeeded(int position) {
        if(position == 0) {
            fragment.gotoPage(listItems.size() - 2, false);
        } else if(position == listItems.size() - 1) {
            fragment.gotoPage(1, false);
        }
    }

    public int getCount() {
        return listItems.size();
    }

    public View createView(int position) {
        if(position == 0) {
            listItems.get(listItems.size() - 2).createView();
        } else if(position == 1) {
            listItems.get(listItems.size() - 1).createView();
        } else if(position == listItems.size() - 2) {
            listItems.get(0).createView();
        } else if(position == listItems.size() - 1) {
            listItems.get(1).createView();
        }
        return listItems.get(position).createView();
    }

    public void destroyView(int position) {
        if(position != 0 && position != 1 && position != listItems.size() - 2 && position != listItems.size() - 1) {
            listItems.get(position).destroyView();
        }
    }

    public void destroyAllViews() {
        for(ListItem item: listItems) {
            item.destroyView();
        }
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
            view = null;
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

}
