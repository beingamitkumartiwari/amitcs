package com.amtee.camscanner.volley_works;

import android.app.Application;
import android.content.Context;
//import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.amtee.camscanner.utilities.LvdTypeface;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by DEVEN SINGH on 7/3/2015.
 */
public class ApplicationFL extends Application {
    public static final String TAG = ApplicationFL.class
            .getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static ApplicationFL mInstance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LvdTypeface.overrideDefaultFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
        mInstance = this;
    }

    public static synchronized ApplicationFL getInstance() {
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
