package com.birbit.android.livecode.twitter.di;

import com.birbit.android.livecode.twitter.App;
import com.birbit.android.livecode.twitter.activity.DMListActivity;
import com.birbit.android.livecode.twitter.activity.MainActivity;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yigit on 2/4/14.
 */
@Module(
        injects = {
                MainActivity.class,
                DMListActivity.class
        }
)
public class AppModule {
    @Provides
    TwitterApiClient.TwitterService provideTwitterService() {
        return App.getInstance().getApiClient().getService();
    }
}
