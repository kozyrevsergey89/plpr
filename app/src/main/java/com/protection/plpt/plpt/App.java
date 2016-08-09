package com.protection.plpt.plpt;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

/**
 * Created by sergii on 7/20/16.
 */
public class App extends MultiDexApplication {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}
