package com.birbit.android.livecode.twitter.job;

import android.content.Context;

import com.birbit.android.livecode.twitter.App;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;

import javax.inject.Singleton;

/**
 * Created by yigit on 2/5/14.
 */
@Singleton
public class MyJobManager extends JobManager {
    public MyJobManager(Context context) {
        super(context, new Configuration.Builder(context)
                .injector(new DependencyInjector() {
                    @Override
                    public void inject(BaseJob baseJob) {
                        App.injectMembers(baseJob);
                    }
                })
 .build());
    }
}
