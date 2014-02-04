package com.birbit.android.livecode.twitter.vo;

import android.text.Html;
import android.text.Spanned;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yigit on 2/1/14.
 */
public class Tweet {
    @SerializedName("id_str")
    private String id;
    @SerializedName("id")
    private long longId;
    @SerializedName("retweeted")
    private boolean retweeted;
    private String text;
    @SerializedName("created_at")
    private String createdAt;
    private User user;
    private Boolean favorited;

    private Spanned uiSpanned;

    public String getId() {
        return id;
    }

    public long getLongId() {
        return longId;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public boolean didFavorite() {
        return Boolean.TRUE.equals(favorited);
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public Spanned getUiSpanned() {
        if(uiSpanned == null) {
            StringBuilder builder = new StringBuilder();
            if(user != null && user.getScreenName() != null) {
                builder.append("<b>").append(Html.escapeHtml(user.getScreenName())).append("</b>:");
            } else {
                builder.append("<b>unknown</b>");
            }
            builder.append(text);
            uiSpanned = Html.fromHtml(builder.toString());
        }
        return uiSpanned;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
}
