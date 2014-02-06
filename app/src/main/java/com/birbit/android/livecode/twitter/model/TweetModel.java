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

import de.greenrobot.dao.DeleteQuery;
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
    private final DeleteQuery<Tweet> deleteByStringId;
    private final Query<Tweet> findByStringIdQuery;

    @Inject
    public TweetModel(DaoSession daoSession) {
        this.daoSession = daoSession;
        tweetDao = daoSession.getTweetDao();
        homeTweetsQuery = tweetDao.queryBuilder().orderDesc(
                TweetDao.Properties.CreatedAt
        ).build();
        topTweetQuery = tweetDao.queryBuilder()
                .where(
                    TweetDao.Properties.Id.notLike(Tweet.LOCAL_ID_PREFIX + "%")
                ).orderDesc(
                        TweetDao.Properties.CreatedAt
                ).limit(1).build();
        deleteByStringId = tweetDao.queryBuilder().where(
                TweetDao.Properties.Id.eq("x")
        ).buildDelete();
        findByStringIdQuery = tweetDao.queryBuilder().where(
                TweetDao.Properties.Id.eq("x")
        ).build();
    }

    public List<Tweet> homeTweets() {
        return homeTweetsQuery.list();
    }

    public Tweet firstHomeTweet() {
        return topTweetQuery.unique();
    }

    public void deleteById(String id) {
        synchronized (deleteByStringId) {
            deleteByStringId.setParameter(0, id);
            deleteByStringId.executeDeleteWithoutDetachingEntities();
        }
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

    public Tweet load(String id) {
        synchronized (findByStringIdQuery) {
            findByStringIdQuery.setParameter(0, id);
            return findByStringIdQuery.unique();
        }
    }

    public void delete(Tweet tweet) {
        tweetDao.delete(tweet);
    }
}
