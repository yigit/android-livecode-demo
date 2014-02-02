package com.birbit.android.livecode.twitter;

import android.app.Application;
import android.os.StrictMode;

import com.birbit.android.livecode.twitter.business.TwitterApiClient;

/**
 * Created by yigit on 2/1/14.
 */
public class App extends Application {
    private static App instance;
    private TwitterApiClient apiClient;
    public App() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = new TwitterApiClient();
        if(Config.ENABLE_STRICT_MODE) {
            enableStrictModel();
        }
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
