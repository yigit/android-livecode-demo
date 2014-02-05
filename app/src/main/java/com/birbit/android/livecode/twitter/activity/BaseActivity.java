package com.birbit.android.livecode.twitter.activity;

import android.app.Activity;
import android.os.Bundle;

import com.birbit.android.livecode.twitter.App;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by yigit on 2/2/14.
 */
public class BaseActivity extends Activity implements LifecycleProvider {
    private State state;
    private CopyOnWriteArraySet<LifecycleListener> lifecycleListeners;

    public BaseActivity() {
        lifecycleListeners = new CopyOnWriteArraySet<LifecycleListener>();
        state = State.constructed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        state = State.created;
        App.injectMembers(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        state = State.started;
        for(LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        state = State.paused;
    }

    @Override
    protected void onResume() {
        super.onResume();
        state = State.resumed;
    }

    @Override
    protected void onStop() {
        super.onStop();
        state = State.stopped;
        for(LifecycleListener lifecycleListener : lifecycleListeners) {
            lifecycleListener.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        state = State.destroyed;
        lifecycleListeners.clear();
    }

    @Override
    public void registerLifecycleListener(LifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    @Override
    public void unregister(LifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    @Override
    public boolean isVisible() {
        return state.ordinal() >= State.started.ordinal()
                && state.ordinal() < State.stopped.ordinal();
    }

    private static enum State {
        constructed,
        created,
        started,
        resumed,
        paused,
        stopped,
        destroyed
    }
}
