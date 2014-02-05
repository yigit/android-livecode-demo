package com.birbit.android.livecode.twitter;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.di.AppModule;
import com.birbit.android.livecode.twitter.util.L;

import dagger.ObjectGraph;

/**
 * Created by yigit on 2/1/14.
 */
public class App extends Application {
    private static App instance;
    private ObjectGraph objectGraph;
    private TwitterApiClient apiClient;
    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = new TwitterApiClient();
        objectGraph = ObjectGraph.create(new AppModule());
        L.getConfig().setLoggingLevel(Log.VERBOSE);
        if(Config.ENABLE_STRICT_MODE) {
            enableStrictModel();
        }
    }

    public static void injectMembers(Object object) {
        getInstance().objectGraph.inject(object);
    }

    private void enableStrictModel() {
        new android.os.Handler().postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyDeath().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog().build());
            }
        });
    }

    public TwitterApiClient getApiClient() {
        return apiClient;
    }

    public static App getInstance() {
        return instance;
    }
}
