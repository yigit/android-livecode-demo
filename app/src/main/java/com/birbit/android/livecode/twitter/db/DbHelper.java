package com.birbit.android.livecode.twitter.db;

import android.database.sqlite.SQLiteDatabase;

import com.birbit.android.livecode.twitter.App;
import com.birbit.android.livecode.twitter.vo.DaoMaster;
import com.birbit.android.livecode.twitter.vo.DaoSession;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yigit on 2/4/14.
 */
@Singleton
public class DbHelper {
    private DaoSession daoSession;
    private DaoMaster daoMaster;
    private SQLiteDatabase db;

    @Inject
    public DbHelper() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(App.getInstance(), "twitter", null);
        db = devOpenHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public DaoMaster getDaoMaster() {
        return daoMaster;
    }
}
