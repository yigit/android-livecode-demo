package com.birbit.android.livecode.twitter.activity;

/**
 * Created by yigit on 2/4/14.
 */
public interface LifecycleProvider {
    public void registerLifecycleListener(LifecycleListener listener);
    public void unregister(LifecycleListener listener);
    public boolean isVisible();
}
