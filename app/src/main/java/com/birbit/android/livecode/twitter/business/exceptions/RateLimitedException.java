package com.birbit.android.livecode.twitter.business.exceptions;

import java.sql.Date;

import retrofit.RetrofitError;

/**
 * Created by yigit on 2/1/14.
 */
public class RateLimitedException extends TwitterApiException {
    private final long limitedUntil;
    RateLimitedException(RetrofitError retrofitError, long xRateLimitReset) {
        super(retrofitError);
        limitedUntil = xRateLimitReset * 1000;
    }

    @Override
    public boolean canRetry() {
        return true;
    }

    public long getLimitedUntil() {
        return limitedUntil;
    }
}
