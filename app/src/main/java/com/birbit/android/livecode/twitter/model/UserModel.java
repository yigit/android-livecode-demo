package com.birbit.android.livecode.twitter.model;

import com.birbit.android.livecode.twitter.vo.DaoSession;
import com.birbit.android.livecode.twitter.vo.User;
import com.birbit.android.livecode.twitter.vo.UserDao;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yigit on 2/4/14.
 */
@Singleton
public class UserModel {
    final UserDao userDao;
    @Inject
    public UserModel(DaoSession daoSession) {
        this.userDao = daoSession.getUserDao();
    }

    public User get(String id) {
        return userDao.load(id);
    }

    public void saveUsers(Collection<User> users) {
        if(users != null) {
            userDao.insertOrReplaceInTx(users);
        }
    }
}
