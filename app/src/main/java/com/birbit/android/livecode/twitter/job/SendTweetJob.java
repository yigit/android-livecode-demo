package com.birbit.android.livecode.twitter.job;

import com.birbit.android.livecode.twitter.Config;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.business.exceptions.TwitterApiException;
import com.birbit.android.livecode.twitter.event.DeletedTweetEvent;
import com.birbit.android.livecode.twitter.event.NewTweetEvent;
import com.birbit.android.livecode.twitter.event.UpdatedTweetEvent;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.vo.DaoSession;
import com.birbit.android.livecode.twitter.vo.Tweet;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by yigit on 2/5/14.
 */
public class SendTweetJob extends Job {
    @Inject transient TweetModel tweetModel;
    @Inject transient EventBus eventBus;
    @Inject transient TwitterApiClient.TwitterService twitterService;
    @Inject transient DaoSession daoSession;
    private final String localTweetId;
    private final String text;

    public SendTweetJob(String text) {
        super(new Params(1).persist().requireNetwork().groupBy("send_tweet"));
        localTweetId = Tweet.createLocalId(UUID.randomUUID().toString());
        this.text = text;
    }

    @Override
    public void onAdded() {
        Tweet tweet = new Tweet();
        tweet.setId(localTweetId);
        tweet.setCreatedAt(new Date());
        tweet.setUserId(Config.USER_ID);
        tweet.setText(text);
        tweetModel.saveTweet(tweet);
        eventBus.post(new NewTweetEvent());
    }

    @Override
    public void onRun() throws Throwable {
        Tweet serverTweet = twitterService.postStatus(text, localTweetId);
        final Tweet localTweet = tweetModel.load(localTweetId);
        if(localTweet != null) {
            localTweet.updateNotNull(serverTweet);
            daoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    tweetModel.deleteById(localTweetId);
                    tweetModel.saveTweet(localTweet);
                }
            });
            eventBus.post(new UpdatedTweetEvent(localTweet));
        } else {
            tweetModel.saveTweet(serverTweet);
            eventBus.post(new NewTweetEvent());
        }
    }

    @Override
    protected void onCancel() {
        //find local and destroy
        Tweet localTweet = tweetModel.load(localTweetId);
        if(localTweet != null) {
            tweetModel.delete(localTweet);
            eventBus.post(new DeletedTweetEvent());
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        if(throwable instanceof TwitterApiException) {
            return ((TwitterApiException) throwable).canRetry();
        }
        //unknown error ?
        return true;
    }
}
