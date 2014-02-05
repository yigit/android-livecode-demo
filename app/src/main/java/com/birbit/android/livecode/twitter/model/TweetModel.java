package com.birbit.android.livecode.twitter.model;

import com.birbit.android.livecode.twitter.vo.DaoSession;
import com.birbit.android.livecode.twitter.vo.Tweet;
import com.birbit.android.livecode.twitter.vo.TweetDao;
import com.birbit.android.livecode.twitter.vo.User;
import com.birbit.android.livecode.twitter.vo.UserDao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.greenrobot.dao.LazyList;
import de.greenrobot.dao.Query;

/**
 * Created by yigit on 2/4/14.
 */
@Singleton
public class TweetModel {
    private final TweetDao tweetDao;
    private final DaoSession daoSession;
    private final Query<Tweet> homeTweetsQuery;
    private final Query<Tweet> topTweetQuery;

    @Inject
    public TweetModel(DaoSession daoSession) {
        this.daoSession = daoSession;
        tweetDao = daoSession.getTweetDao();
        homeTweetsQuery = tweetDao.queryBuilder().orderDesc(
                TweetDao.Properties.CreatedAt
        ).build();
        topTweetQuery = tweetDao.queryBuilder().orderDesc(
                TweetDao.Properties.CreatedAt
        ).limit(1).build();
    }

    public List<Tweet> homeTweets() {
        return homeTweetsQuery.list();
    }

    public Tweet firstHomeTweet() {
        return topTweetQuery.unique();
    }

    public void saveTweet(Tweet tweet) {
        if(tweet != null) {
            tweetDao.insertOrReplace(tweet);
        }
    }

    public void saveTweets(final List<Tweet> tweets) {
        if(tweets != null) {
            tweetDao.insertOrReplaceInTx(tweets);
        }
    }
}
