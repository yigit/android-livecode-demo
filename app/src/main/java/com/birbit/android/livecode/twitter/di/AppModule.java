package com.birbit.android.livecode.twitter.di;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.birbit.android.livecode.twitter.App;
import com.birbit.android.livecode.twitter.activity.MainActivity;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.db.DbHelper;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.model.UserModel;
import com.birbit.android.livecode.twitter.vo.DaoMaster;
import com.birbit.android.livecode.twitter.vo.DaoSession;
import com.birbit.android.livecode.twitter.vo.UserDao;

import dagger.Module;
import dagger.Provides;

/**
 * Created by yigit on 2/4/14.
 */
@Module(
        injects = {
                MainActivity.class,
                TweetModel.class,
                UserModel.class
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



}
