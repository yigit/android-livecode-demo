package com.birbit.android.livecode.twitter.activity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.birbit.android.livecode.twitter.App;
import com.birbit.android.livecode.twitter.R;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.util.AsyncTaskWithProgress;
import com.birbit.android.livecode.twitter.vo.Tweet;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by yigit on 2/1/14.
 */
public class MainActivity extends BaseActivity {
    private ListView tweetList;
    @Inject TwitterApiClient.TwitterService twitterService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        initUI();
        loadTweets();
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
        new AsyncTaskWithProgress<Tweet>(this, R.string.post_tweet_progress_msg) {
            @Override
            protected Tweet doInBackground(Void... params) {
                return twitterService.postStatus(trimmed);
            }

            @Override
            protected void onPostExecute(Tweet tweet) {
                super.onPostExecute(tweet);
                //reload tweet
                loadTweets();
            }
        }.execute();
    }

    private void loadTweets() {
        new AsyncTaskWithProgress<List<Tweet>>(this, null) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected List<Tweet> doInBackground(Void... params) {
                return twitterService.homeTimeline();
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            protected void onPostExecute(List<Tweet> tweets) {
                super.onPostExecute(tweets);
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
            return getItem(position).getLongId();
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
                protected Void doInBackground(Void... params) {
                    if(favorite) {
                        twitterService.favorite(tweet.getId());
                    } else {
                        twitterService.removeFavorite(tweet.getId());
                    }
                    tweet.setFavorited(favorite);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    notifyDataSetChanged();
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
                protected Void doInBackground(Void... params) {
                    twitterService.retweet(tweet.getId());
                    tweet.setRetweeted(true);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    notifyDataSetChanged();
                    loadTweets();
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
