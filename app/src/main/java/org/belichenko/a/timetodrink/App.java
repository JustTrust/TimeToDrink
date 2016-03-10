package org.belichenko.a.timetodrink;

import android.app.Application;
import android.content.Context;

/**
 * App class for getting context
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