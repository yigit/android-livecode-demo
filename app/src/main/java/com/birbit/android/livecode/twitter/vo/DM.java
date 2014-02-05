package com.birbit.android.livecode.twitter.vo;

import com.google.gson.annotations.SerializedName;

import com.birbit.android.livecode.twitter.util.L;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yigit on 2/2/14.
 */
public class DM {
    @SerializedName("id_str")
    private String id;
    private User recipient;
    private User sender;
    private String text;
    @SerializedName("created_at")
    private String createdAt;
    private Date createdDate;

    private transient SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");

    public String getId() {
        return id;
    }

    public Date getCreatedDate() {
        if(createdDate == null && createdAt != null) {
            try {
                createdDate = dateFormat.parse(createdAt);
            } catch (ParseException e) {
                L.e(e, "error while parsing date %s", createdAt);
            }
        }
        return createdDate;
    }

    public User getRecipient() {
        return recipient;
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }
}
