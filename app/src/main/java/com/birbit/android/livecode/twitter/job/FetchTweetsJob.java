package com.birbit.android.livecode.twitter.job;

import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.business.exceptions.TwitterApiException;
import com.birbit.android.livecode.twitter.event.NewTweetEvent;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.model.UserModel;
import com.birbit.android.livecode.twitter.vo.Tweet;
import com.birbit.android.livecode.twitter.vo.User;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by yigit on 2/5/14.
 */
public class FetchTweetsJob extends Job {
    @Inject transient TweetModel tweetModel;
    @Inject transient UserModel userModel;
    @Inject transient TwitterApiClient.TwitterService twitterService;
    @Inject transient EventBus eventBus;
    static AtomicLong counter = new AtomicLong();
    private final long id;

    public FetchTweetsJob() {
        super(new Params(2).groupBy("fetch_home").requireNetwork());
        id = counter.incrementAndGet();
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if(id != counter.get()) {
            //fail silently, is not me
            return;
        }
        Tweet topTweet = tweetModel.firstHomeTweet();
        List<Tweet> tweetList = twitterService.homeTimeline(
                topTweet == null ? null : topTweet.getId()
        );
        if(tweetList.size() > 0) {
            Map<String, User> users = new HashMap<String, User>();
            for(Tweet tweet : tweetList) {
                if(tweet.getUser() != null) {
                    users.put(tweet.getUserId(), tweet.getUser());
                }
            }
            userModel.saveUsers(users.values());
            tweetModel.saveTweets(tweetList);
            eventBus.post(new NewTweetEvent());
        }

    }

    @Override
    protected void onCancel() {

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
