package com.birbit.android.livecode.twitter.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yigit on 2/1/14.
 */
public class Tweet {
    private long id;
    private boolean retweeted;
    private String text;
    @SerializedName("created_at")
    private String createdAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
