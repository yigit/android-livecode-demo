package com.birbit.android.livecode.twitter.event;

import com.birbit.android.livecode.twitter.vo.Tweet;

/**
 * Created by yigit on 2/5/14.
 */
public class UpdatedTweetEvent {
    private final Tweet tweet;

    public UpdatedTweetEvent(Tweet tweet) {
        this.tweet = tweet;
    }

    public Tweet getTweet() {
        return tweet;
    }
}
