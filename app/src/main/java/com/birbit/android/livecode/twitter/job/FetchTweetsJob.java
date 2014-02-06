package com.birbit.android.livecode.twitter.job;

import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.business.exceptions.TwitterApiException;
import com.birbit.android.livecode.twitter.events.NewTweetsEvent;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.model.UserModel;
import com.birbit.android.livecode.twitter.vo.Tweet;
import com.birbit.android.livecode.twitter.vo.User;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * Created by yigit on 2/5/14.
 */
public class FetchTweetsJob extends Job {
    @Inject TweetModel model;
    @Inject UserModel userModel;
    @Inject TwitterApiClient.TwitterService twitterService;
    @Inject EventBus eventBus;
    public FetchTweetsJob() {
        super(new Params(2)
        .requireNetwork()
        .groupBy("home_feed_fetch"));
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Tweet tweet = model.firstHomeTweet();
        List<Tweet> newTweets = twitterService.homeTimeline(
                tweet == null ? null : tweet.getId()
        );
        if(newTweets.size() > 0) {
            Map<String, User> users = new HashMap<String, User>();
            for(Tweet t : newTweets) {
                users.put(t.getUserId(), t.getUser());
            }
            userModel.saveUsers(users.values());
            model.saveTweets(newTweets);
            eventBus.post(new NewTweetsEvent());
        }
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        if(throwable instanceof TwitterApiException) {
            return  ((TwitterApiException) throwable).canRetry();
        }
        return true;
    }
}
