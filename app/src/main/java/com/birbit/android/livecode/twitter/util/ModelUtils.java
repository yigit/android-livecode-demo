package com.birbit.android.livecode.twitter.util;

import android.content.Context;

import com.birbit.android.livecode.twitter.vo.CachesUIData;

import java.util.Collection;

/**
 * Created by yigit on 2/4/14.
 */
public class ModelUtils {
    public static <T extends CachesUIData> void cacheUIData(Context context, Collection<T> data) {
        for(T t : data) {
            t.cacheUIData(context);
        }
    }
}
