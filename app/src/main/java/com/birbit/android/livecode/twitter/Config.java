package com.birbit.android.livecode.twitter;

import android.util.TimeUtils;

import java.util.concurrent.TimeUnit;

/**
 * Created by yigit on 2/1/14.
 */
public class Config {
    public static final String API_BASE = "https://api.twitter.com/1.1";
    public static final long ARTIFICIAL_REQUEST_DELAY = TimeUnit.SECONDS.toMillis(1);//MS
    public static final boolean ENABLE_STRICT_MODE = true;
    public static final ConnectionConfiguration CONNECTION_CONFIGURATION = ConnectionConfiguration.LIVECODE_DEMO_1;

    public static enum ConnectionConfiguration {
        JOBQUEUE_TEST_APP("GfonALJ3wScsJfPsjLpl5g", "7EYvqptQeBQ9FFcbaPHv0WVe9rRbDi8dmX9DffIMIE",
                "1443060589-h6JU83NsHMYx5M47Is2RzlVZmvHPbxQND9xT6KQ", "QLut9Mgwge5WptlVnCz9wxmbJrqBFNazkEYrGDZKYE"),
        JOBQUEUE_TEST_APP_2("APEGHy66BMYzvgEktDfc1Q","wf8XXYwivxHQtiIqPSD3lpq6po9JGRyYBIX0lT0",
                "1443060589-kYyfw38vXsQuZAwUFxmyXzDW4SIhTIK22sIL2r2","H1NGNScL7UVNRFILT3SUF3t087p7KCsbyPTZOEbJzY3hd"),
        LIVECODE_DEMO_1("wEIMoUSx5St3KOYx1NxPQ", "rHMma9V97DlgBt8G0hEwRo4yHs0QHPwjRLr9cC3cGNc",
                "1443060589-DVRvnx1sUfqf73arhU8G7icabNlpVxWT9MaVN0t", "TGtukAgzNgNFTcaQlOfwqlpCFpu6h3fy8rwmcmfryJ1Bc")
        ;
        public final String consumerKey;
        public final String consumerSecret;
        public final String accessToken;
        public final String accessTokenSecret;
        ConnectionConfiguration(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
            this.accessToken = accessToken;
            this.accessTokenSecret = accessTokenSecret;
        }
    }
}
