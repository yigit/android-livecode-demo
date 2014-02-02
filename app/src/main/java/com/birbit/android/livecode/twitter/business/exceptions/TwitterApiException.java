package com.birbit.android.livecode.twitter.business.exceptions;

import com.birbit.android.livecode.twitter.util.L;

import org.apache.http.Header;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by yigit on 2/1/14.
 */
public class TwitterApiException extends RuntimeException {
    private final RetrofitError retrofitError;
    TwitterApiException(RetrofitError retrofitError) {
        setStackTrace(retrofitError.getStackTrace());
        this.retrofitError = retrofitError;
    }

    public boolean canRetry() {
        int status = retrofitError.getResponse().getStatus();
        return status < 400 || status > 499;
    }

    public static final TwitterApiException convert(RetrofitError retrofitError) {
        try {
            for(retrofit.client.Header header : retrofitError.getResponse().getHeaders()) {
                if("x-rate-limit-reset".equals(header.getName())) {
                    return new RateLimitedException(retrofitError, Long.parseLong(header.getValue()));
                }
            }
        } catch (Throwable t) {
            L.e(t);
        }
        return new TwitterApiException(retrofitError);
    }
}
