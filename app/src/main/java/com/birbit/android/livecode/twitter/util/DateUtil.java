package com.birbit.android.livecode.twitter.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yigit on 2/4/14.
 */
public class DateUtil {
    private static final DateFormat TWITTER_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");

    public static Date parseDate(String dateStr) {
        try {
            return TWITTER_DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            L.e(e, "cannot parse date string %s", dateStr);
        }
        return null;
    }

}
