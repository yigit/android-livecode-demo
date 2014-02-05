package com.birbit.android.livecode.twitter.vo;






// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table TWEET.
 */
abstract public class TweetBase implements com.birbit.android.livecode.twitter.vo.CachesUIData {

    protected Long localId;
    @com.google.gson.annotations.SerializedName( "id_str" )
    protected String id;
    @com.google.gson.annotations.SerializedName( "retweeted" )
    protected Boolean retweeted;
    protected String text;
    protected java.util.Date createdAt;
    protected String userId;
    protected Boolean favorited;




    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public TweetBase() {
    }

    public TweetBase(Long localId) {
        this.localId = localId;
    }

    public TweetBase(Long localId, String id, Boolean retweeted, String text, java.util.Date createdAt, String userId, Boolean favorited) {
        this.localId = localId;
        this.id = id;
        this.retweeted = retweeted;
        this.text = text;
        this.createdAt = createdAt;
        this.userId = userId;
        this.favorited = favorited;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getRetweeted() {
        return retweeted;
    }

    public void setRetweeted(Boolean retweeted) {
        this.retweeted = retweeted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(Boolean favorited) {
        this.favorited = favorited;
    }

    public void updateNotNull(Tweet other) {
        if(this == other) {
            return;//both came from db, no need to run this.
        }

        if(other.localId != null) {
            this.localId = other.localId;
        }


        if(other.id != null) {
            this.id = other.id;
        }


        if(other.retweeted != null) {
            this.retweeted = other.retweeted;
        }


        if(other.text != null) {
            this.text = other.text;
        }


        if(other.createdAt != null) {
            this.createdAt = other.createdAt;
        }


        if(other.userId != null) {
            this.userId = other.userId;
        }


        if(other.favorited != null) {
            this.favorited = other.favorited;
        }

        // relationships
    }


    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

    public void onBeforeSave() {
        //you can override this method and do some stuff if you want to :)

    }
}
