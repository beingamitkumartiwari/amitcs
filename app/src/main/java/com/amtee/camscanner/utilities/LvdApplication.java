package com.amtee.camscanner.utilities;

import android.app.Application;

/**
 * Created by Deven on 18-04-2015.
 */
public class LvdApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LvdTypeface.overrideDefaultFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf");
    }
}
