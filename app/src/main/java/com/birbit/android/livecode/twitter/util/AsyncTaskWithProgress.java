package com.birbit.android.livecode.twitter.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * Created by yigit on 2/2/14.
 */
abstract public class AsyncTaskWithProgress<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private ProgressDialog progressDialog;
    protected AsyncTaskWithProgress(Context context, int textId) {
        this(context, context.getResources().getString(textId));
    }
    protected AsyncTaskWithProgress(Context context, String text) {
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
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        progressDialog.dismiss();
    }

    @Override
    protected void onCancelled(Result result) {
        super.onCancelled(result);
        progressDialog.dismiss();
    }
}
