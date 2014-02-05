package com.birbit.android.livecode.twitter.activity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.birbit.android.livecode.twitter.R;
import com.birbit.android.livecode.twitter.business.TwitterApiClient;
import com.birbit.android.livecode.twitter.util.AsyncTaskWithProgress;
import com.birbit.android.livecode.twitter.vo.DM;
import com.birbit.android.livecode.twitter.vo.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yigit on 2/2/14.
 */
public class DMListActivity extends BaseActivity {

    @Inject TwitterApiClient.TwitterService twitterService;

    @InjectView(R.id.list_view) ListView dmListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_dm_list);
        ButterKnife.inject(this);
        fetchDMList();
    }

    private void fetchDMList() {
        new AsyncTaskWithProgress<Map<User, Set<DM>>>(this, null) {

            final Map<User, Set<DM>> dmMap = new TreeMap<User, Set<DM>>(new Comparator<User>() {
                @Override
                public int compare(User lhs, User rhs) {
                    Map<User, Set<DM>> clonedMap = new HashMap<User, Set<DM>>(dmMap);
                    Set<DM> lSet = clonedMap.get(lhs);
                    Set<DM> rSet = clonedMap.get(rhs);
                    if (rSet == null) {
                        return -1;
                    }
                    if (lSet == null) {
                        return 1;
                    }
                    return lSet.iterator().next().getCreatedDate().compareTo(rSet.iterator().next().getCreatedDate());
                }
            });

            private Comparator<DM> dmComparator = new Comparator<DM>() {
                @Override
                public int compare(DM lhs, DM rhs) {
                    return lhs.getCreatedDate().compareTo(rhs.getCreatedDate());
                }
            };

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                setProgressBarIndeterminateVisibility(true);
            }

            @Override
            protected Map<User, Set<DM>> doInBackground(Void... params) {
                List<DM> receivedList = twitterService.getReceivedDMs(50);
                List<DM> sentList = twitterService.getSentDMs(50);

                for (DM dm : receivedList) {
                    User userKey = dm.getSender();
                    Set<DM> dmSet = dmMap.get(userKey);
                    if (dmSet == null) {
                        dmSet = new TreeSet<DM>(dmComparator);
                    }
                    dmSet.add(dm);
                    dmMap.put(userKey, dmSet);
                }

                for (DM dm : sentList) {
                    User userKey = dm.getRecipient();
                    Set<DM> dmSet = dmMap.get(userKey);
                    if (dmSet == null) {
                        dmSet = new TreeSet<DM>(dmComparator);
                    }
                    dmSet.add(dm);
                    dmMap.put(userKey, dmSet);
                }

                return dmMap;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            protected void onPostExecute(Map<User, Set<DM>> dmMap) {
                super.onPostExecute(dmMap);
                setProgressBarIndeterminateVisibility(false);
                dmListView.setAdapter(new DMAdapter(dmMap));
            }
        }.execute();
    }

    private class DMAdapter extends BaseAdapter {

        final Map<User, Set<DM>> dmMap;
        final List<User> userList;

        private DMAdapter(Map<User, Set<DM>> dmMap) {
            this.dmMap = dmMap;
            userList = new ArrayList<User>(dmMap.keySet());
        }

        @Override
        public int getCount() {
            return dmMap.size();
        }

        @Override
        public Pair<User, DM> getItem(int position) {
            User user = userList.get(position);
            return new Pair<User, DM>(user, dmMap.get(user).iterator().next());
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).first.getId().hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.dm_list_item, parent, false);
            }
            return ViewHolder.from(convertView).render(getItem(position));
        }
    }

    public static class ViewHolder {

        final View content;

        @InjectView(R.id.avatar) ImageView avatar;
        @InjectView(R.id.username) TextView userName;
        @InjectView(R.id.preview) TextView preview;

        public ViewHolder(View view) {
            content = view;
            ButterKnife.inject(this, content);
        }

        public View render(Pair<User, DM> userDm) {
            userName.setText(userDm.first.getScreenName());
            preview.setText(userDm.second.getText());
            return content;
        }

        public static ViewHolder from(View view) {
            Object tag = view.getTag();
            if(tag instanceof ViewHolder) {
                return (ViewHolder) tag;
            }
            else return new ViewHolder(view);
        }
    }

}
