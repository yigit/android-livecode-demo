package com.birbit.android.livecode.twitter.di;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.birbit.android.livecode.twitter.App;
import com.birbit.android.livecode.twitter.activity.MainActivity;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.db.DbHelper;
import com.birbit.android.livecode.twitter.job.FetchTweetsJob;
import com.birbit.android.livecode.twitter.job.MyJobManager;
import com.birbit.android.livecode.twitter.job.SendTweetJob;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.model.UserModel;
import com.birbit.android.livecode.twitter.vo.DaoMaster;
import com.birbit.android.livecode.twitter.vo.DaoSession;
import com.birbit.android.livecode.twitter.vo.UserDao;
import com.google.common.eventbus.EventBus;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yigit on 2/4/14.
 */
@Module(
        injects = {
                MainActivity.class,
                TweetModel.class,
                UserModel.class,
                EventBus.class,
                SendTweetJob.class,
                FetchTweetsJob.class
        }
)
public class AppModule {
    @Provides
    TwitterApiClient.TwitterService provideTwitterService() {
        return App.getInstance().getApiClient().getService();
    }

    @Provides
    DaoSession provideDaoSession(DbHelper dbHelper) {
        return dbHelper.getDaoSession();
    }


    @Provides
    @Singleton
    de.greenrobot.event.EventBus provideEventBus() {
        return de.greenrobot.event.EventBus.getDefault();
    }

    @Provides
    @Singleton
    JobManager provideJobManager() {
        return new MyJobManager(App.getInstance());
    }


}
