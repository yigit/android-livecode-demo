package com.birbit.android.livecode.twitter.vo;

import com.birbit.android.livecode.twitter.util.L;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
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

    public String getId() {
        return id;
    }

    public Date getCreatedDate() {
        if(createdDate == null && createdAt != null) {
            try {
                createdDate = DateFormat.getInstance().parse(createdAt);
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
