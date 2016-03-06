package ru.sibsoft.mikhailv.pictures;

import android.app.Application;

import com.octo.android.robospice.SpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBitmapObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.memory.LruCacheBitmapObjectPersister;

/**
 * Created by mikhailv on 2/10/16.
 */
public class LoadImageService extends SpiceService {
    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        final CacheManager cacheManager = new CacheManager();
        cacheManager.addPersister(new LruCacheBitmapObjectPersister(new InFileBitmapObjectPersister(application), 1024 * 1024 * 4));
        return cacheManager;
    }

    @Override
    public int getThreadCount() {
        return 3;
    }
}
