package com.birbit.android.livecode.twitter.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.birbit.android.livecode.twitter.activity.BaseActivity;
import com.birbit.android.livecode.twitter.activity.LifecycleListener;
import com.birbit.android.livecode.twitter.activity.LifecycleProvider;

/**
 * Created by yigit on 2/2/14.
 */
abstract public class AsyncTaskWithProgress<Result> extends AsyncTask<Void, Void, Result> implements LifecycleListener {
    private ProgressDialog progressDialog;
    private BaseActivity lifecycleProvider;
    private Throwable runException;

    protected AsyncTaskWithProgress(BaseActivity context, int textId) {
        this(context, context.getResources().getString(textId));
    }
    protected AsyncTaskWithProgress(BaseActivity context, String text) {
        lifecycleProvider = context;
        context.registerLifecycleListener(this);
        if(text != null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(text);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(false);
                }
            });
        }
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    final protected Result doInBackground(Void... params) {
        try {
            return safeDoInBackground(params);
        } catch (Throwable t) {
            runException = t;
            L.e(runException, "exception in async task");
        }
        return null;
    }

    protected abstract Result safeDoInBackground(Void[] params);
    protected abstract void safeOnPostExecute(Result result);

    @Override
    final protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        final BaseActivity context = lifecycleProvider;
        cleanup();
        if(runException == null) {
            safeOnPostExecute(result);
        } else if(context.isVisible()) {
            new AlertDialog.Builder(context).setMessage(runException.getMessage()).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        cleanup();
    }

    @Override
    protected void onCancelled(Result result) {
        super.onCancelled(result);
        if(progressDialog != null) {
            progressDialog.dismiss();
        }
        cleanup();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        cancel(false);
        cleanup();
    }

    private void cleanup() {
        if(lifecycleProvider != null) {
            L.d("cleaning up async task %s", getClass().getName());
            lifecycleProvider.unregister(this);
            lifecycleProvider = null;
        }
    }
}
