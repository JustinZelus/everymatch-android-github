package com.everymatch.saas.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Helper class that is used to provide references to initialized RequestQueue(s) and ImageLoader(s)
 */
public class VolleyHelper {

    private static final String TAG = VolleyHelper.class.getSimpleName();

    // For manually storing bitmaps
    private static RequestQueue sRequestQueue;
    private static ImageLoader sImageLoader;

    public static final ImageLoader.ImageListener EMPTY_IMAGE_LISTENER = new ImageLoader.ImageListener() {
        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {}
        @Override
        public void onErrorResponse(VolleyError error) {}
    };

    private VolleyHelper() {
        Log.i(TAG, "VolleyHelper");
    }

    public static void init(Context context) {
        Log.i(TAG, "init");
        sRequestQueue = Volley.newRequestQueue(context);
        //sImageLoader = new ImageLoader(Volley.newRequestQueue(context), new BitmapLruCache());
    }

    public static RequestQueue getRequestQueue() {
        Log.i(TAG, "getRequestQueue");
        if (sRequestQueue != null) {
            return sRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static ImageLoader getImageLoader() {
        return sImageLoader;
    }


    public static class BitmapLruCache
            extends LruCache<String, Bitmap>
            implements ImageLoader.ImageCache {

        public BitmapLruCache() {
            this(getDefaultLruCacheSize());
        }

        public BitmapLruCache(int sizeInKiloBytes) {
            super(sizeInKiloBytes);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }

        public static int getDefaultLruCacheSize() {
            final int maxMemory =
                    (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;

            return cacheSize;
        }
    }

}