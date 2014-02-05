package com.birbit.android.livecode.twitter.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.birbit.android.livecode.twitter.R;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.model.TweetModel;
import com.birbit.android.livecode.twitter.model.UserModel;
import com.birbit.android.livecode.twitter.util.AsyncTaskWithProgress;
import com.birbit.android.livecode.twitter.util.ModelUtils;
import com.birbit.android.livecode.twitter.vo.Tweet;
import com.birbit.android.livecode.twitter.vo.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by yigit on 2/1/14.
 */
public class MainActivity extends BaseActivity {
    private ListView tweetList;
    @Inject TwitterApiClient.TwitterService twitterService;
    @Inject TweetModel tweetModel;
    @Inject UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadTweets();
        fetchTweets();
    }

    private void initUI() {
        tweetList = ((ListView) findViewById(R.id.tweet_list));
        findViewById(R.id.send_tweet_button).setOnClickListener(onSendTweetClick);
    }

    private View.OnClickListener onSendTweetClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText sendTextView = (EditText) findViewById(R.id.post_tweet_edit_text);
            String trimmed = sendTextView.getText().toString().trim();
            if(trimmed.length() == 0) {
                return;
            }
            if(trimmed.length() > 140) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(R.string.error_too_long)
                        .show();
                return;
            }
            sendTextView.setText("");
            sendTweet(trimmed);
        }
    };

    private void sendTweet(final String trimmed) {
        new AsyncTaskWithProgress<Void>(this, R.string.post_tweet_progress_msg) {
            @Override
            protected Void safeDoInBackground(Void... params) {
                Tweet tweet = twitterService.postStatus(trimmed);
                tweetModel.saveTweet(tweet);
                return null;
            }

            @Override
            protected void safeOnPostExecute(Void res) {
                //reload tweet
                reloadTweets();
            }
        }.execute();
    }

    private void fetchTweets() {
        new AsyncTaskWithProgress<Void>(this, null) {
            @Override
            protected Void safeDoInBackground(Void... params) {
                //get top tweet
                Tweet topTweeet = tweetModel.firstHomeTweet();
                List<Tweet> tweets = twitterService.homeTimeline(topTweeet == null ? null : topTweeet.getId());
                Map<String, User> users = new HashMap<String, User>();
                for(Tweet tweet : tweets) {
                    users.put(tweet.getUser().getId(), tweet.getUser());
                }
                tweetModel.saveTweets(tweets);
                userModel.saveUsers(users.values());
                return null;
            }

            @Override
            protected void safeOnPostExecute(Void aVoid) {
                reloadTweets();
            }
        }.execute();
    }

    private void reloadTweets() {
        new AsyncTaskWithProgress<List<Tweet>>(this, null) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected List<Tweet> safeDoInBackground(Void... params) {
                List<Tweet> tweets = tweetModel.homeTweets();
                ModelUtils.cacheUIData(MainActivity.this, tweets);
                return tweets;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            protected void safeOnPostExecute(List<Tweet> tweets) {
                setProgressBarIndeterminateVisibility(false);
                tweetList.setAdapter(new TweetAdapter(tweets));
            }
        }.execute();
    }

    private final class TweetAdapter extends BaseAdapter implements ViewHolder.Listener {
        final List<Tweet> tweets;
        public TweetAdapter(List<Tweet> tweets) {
            this.tweets = tweets;
        }
        @Override
        public int getCount() {
            return tweets.size();
        }

        @Override
        public Tweet getItem(int position) {
            return tweets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.tweet_list_item, parent, false);
            }
            return ViewHolder.from(convertView, this).render(getItem(position));
        }

        @Override
        public void onFavorite(final Tweet tweet) {
            final boolean favorite = tweet.didFavorite() == false;
            new AsyncTaskWithProgress<Void>(MainActivity.this, favorite
                ? R.string.favorite_remove_progress_msg : R.string.favorite_add_progress_msg) {

                @Override
                protected Void safeDoInBackground(Void... params) {
                    Tweet result;
                    if(favorite) {
                        result = twitterService.favorite(tweet.getId());
                    } else {
                        result = twitterService.removeFavorite(tweet.getId());
                    }
                    tweetModel.saveTweet(result);
                    return null;
                }

                @Override
                protected void safeOnPostExecute(Void aVoid) {
                    reloadTweets();
                }
            }.execute();
        }

        @Override
        public void onRetweet(final Tweet tweet) {
            if(tweet.isRetweeted()) {
                return;
            }
            new AsyncTaskWithProgress<Void>(MainActivity.this, R.string.re_tweet_progress_msg) {

                @Override
                protected Void safeDoInBackground(Void... params) {
                    Tweet result = twitterService.retweet(tweet.getId());
                    tweetModel.saveTweet(result);
                    return null;
                }

                @Override
                protected void safeOnPostExecute(Void aVoid) {
                    reloadTweets();
                }
            }.execute();

        }
    }

    public static class ViewHolder implements View.OnClickListener {
        final View root;
        final TextView textView;
        final ImageView retweetButton;
        final ImageView favoriteButton;
        final Listener listener;

        Tweet lastTweet;

        public ViewHolder(View view, Listener listener) {
            root = view;
            this.listener = listener;
            textView = (TextView) view.findViewById(R.id.content);
            retweetButton = (ImageView) view.findViewById(R.id.retweet_button);
            favoriteButton = (ImageView) view.findViewById(R.id.favorite_button);
            retweetButton.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
        }

        public View render(Tweet tweet) {
            lastTweet = tweet;
            textView.setText(tweet.getUiSpanned());
            retweetButton.setImageResource(
                    tweet.isRetweeted() ? R.drawable.retweet : R.drawable.retweet_black);
            favoriteButton.setImageResource(
                    tweet.didFavorite() ? R.drawable.favorite_full : R.drawable.favorite_black);
            return root;
        }

        public static ViewHolder from(View view, Listener listener) {
            Object tag = view.getTag();
            if(tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }
            else return new ViewHolder(view, listener);
        }

        @Override
        public void onClick(View v) {
            if(lastTweet == null) {
                return;
            }
            if(v == favoriteButton) {
                listener.onFavorite(lastTweet);
            } else if(v == retweetButton) {
                listener.onRetweet(lastTweet);
            }
        }

        public static interface Listener {
            public void onFavorite(Tweet tweet);
            public void onRetweet(Tweet tweet);
        }
    }
}
