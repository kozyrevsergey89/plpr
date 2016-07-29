package com.protection.plpt.plpt;

import android.app.Application;
import android.content.Context;

/**
 * Created by sergii on 7/20/16.
 */
public class App extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }
}
