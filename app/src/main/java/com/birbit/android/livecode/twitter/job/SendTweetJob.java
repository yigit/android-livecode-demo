package com.birbit.android.livecode.twitter.job;

import com.birbit.android.livecode.twitter.Config;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.business.exceptions.TwitterApiException;
import com.birbit.android.livecode.twitter.events.DeletedTweetEvent;
import com.birbit.android.livecode.twitter.events.NewTweetsEvent;
import com.birbit.android.livecode.twitter.events.UpdatedTweetEvent;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.model.UserModel;
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
    @Inject transient TweetModel model;
    @Inject transient UserModel userModel;
    @Inject transient TwitterApiClient.TwitterService twitterService;
    @Inject transient EventBus eventBus;

    private String text;
    private String uniqId;
    public SendTweetJob(String text) {
        super(new Params(1).groupBy("send_tweet")
        .persist()
        .requireNetwork());
        this.text = text;
        uniqId = Tweet.createLocalId(UUID.randomUUID().toString());
    }

    @Override
    public void onAdded() {
        Tweet tweet = new Tweet();
        tweet.setId(uniqId);
        tweet.setText(text);
        tweet.setUserId(Config.USER_ID);
        tweet.setCreatedAt(new Date());
        model.saveTweet(tweet);
        eventBus.post(new NewTweetsEvent());
    }

    @Override
    public void onRun() throws Throwable {
        Tweet tweet = twitterService.postStatus(text);
        Tweet localTweet = model.load(uniqId);
        if(localTweet != null) {
            localTweet.updateNotNull(tweet);
            model.saveTweet(localTweet);
            eventBus.post(new UpdatedTweetEvent(localTweet));
        } else {
            model.saveTweet(tweet);
            eventBus.post(new NewTweetsEvent());
        }
    }

    @Override
    protected void onCancel() {
        Tweet localTweet = model.load(uniqId);
        if(localTweet != null) {
            model.deleteById(localTweet.getId());
            eventBus.post(new DeletedTweetEvent());
        }
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        if(throwable instanceof TwitterApiException) {
            return  ((TwitterApiException) throwable).canRetry();
        }
        return true;
    }
}
