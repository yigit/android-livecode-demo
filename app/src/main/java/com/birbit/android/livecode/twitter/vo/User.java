package com.birbit.android.livecode.twitter.vo;

/**
 * Created by yigit on 2/2/14.
 */

import com.google.gson.annotations.SerializedName;

public class User {
    private String description;
    private boolean following;
    @SerializedName("id_str")
    private String id;
    @SerializedName("screen_name")
    private String screenName;

    public String getDescription() {
        return description;
    }

    public boolean isFollowing() {
        return following;
    }

    public String getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }
}
