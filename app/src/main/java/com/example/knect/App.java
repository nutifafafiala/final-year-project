package com.example.knect;

import android.app.Application;

import nouri.in.goodprefslib.GoodPrefs;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        GoodPrefs.init(getApplicationContext());
    }
}
