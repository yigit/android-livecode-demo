package com.birbit.android.livecode.twitter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.birbit.android.livecode.twitter.vo.Tweet;

import java.util.List;

/**
 * Created by yigit on 2/1/14.
 */
public class MainActivity extends Activity {
    private ListView tweetList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        tweetList = ((ListView) findViewById(R.id.tweet_list));
        loadTweets();
    }

    private void loadTweets() {
        new AsyncTask<Void, Void, List<Tweet>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected List<Tweet> doInBackground(Void... params) {
                return App.getInstance().getApiClient().getService().homeTimeline();
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

    private final class TweetAdapter extends BaseAdapter {
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
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new TextView(MainActivity.this);
            }
            ((TextView) convertView).setText(getItem(position).getText());
            return convertView;
        }
    }
}
