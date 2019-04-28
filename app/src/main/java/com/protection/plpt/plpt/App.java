package com.protection.plpt.plpt;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;

/**
 * Created by sergii on 7/20/16.
 */
public class App extends MultiDexApplication {

    private static Context context;

    public static Context getAppContext() {
        return App.context;
    }

    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        App.context = getApplicationContext();
    }
}
