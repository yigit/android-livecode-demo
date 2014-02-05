package com.birbit;

import de.greenrobot.daogenerator.Annotation;
import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by yigit on 2/4/14.
 */
public class TwitterDaoGenerator extends DaoGenerator {
    public TwitterDaoGenerator() throws IOException {
    }

    public static void main(String[] args) {
        Schema schema = new Schema(1, "com.birbit.android.livecode.twitter.vo");
        schema.setDefaultJavaPackageTest("com.birbit.android.livecode.twitter.test.vo");
        schema.setDefaultJavaPackageDao("com.birbit.android.livecode.twitter.vo");
        schema.enableKeepSectionsByDefault();
        addTweet(schema);
        addUser(schema);
        try {
            new DaoGenerator().generateAll(schema, "../app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class SerializedName extends Annotation {
        public SerializedName(String val) {
            super("com.google.gson.annotations.SerializedName", '"' + val + '"');
        }
    }

    private static void addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        user.addStringProperty("id").columnName("_id").primaryKey().addFieldAnnotation(new SerializedName("id_str"));
        user.addStringProperty("description");
        user.addBooleanProperty("following");
        user.addStringProperty("screenName").addFieldAnnotation(new SerializedName("screen_name"));
    }

    private static void addTweet(Schema schema) {
        Entity tweet = schema.addEntity("Tweet");
        tweet.implementsInterface("com.birbit.android.livecode.twitter.vo.CachesUIData");
        tweet.addLongProperty("longId").columnName("_id").primaryKey().addFieldAnnotation(new SerializedName("id"));
        tweet.addStringProperty("id").columnName("id_str").addFieldAnnotation(new SerializedName("id_str"));
        tweet.addBooleanProperty("retweeted").addFieldAnnotation(new SerializedName("retweeted"));
        tweet.addStringProperty("text");
        tweet.addDateProperty("createdAt");
        tweet.addStringProperty("userId");
        tweet.addBooleanProperty("favorited");
    }
}
