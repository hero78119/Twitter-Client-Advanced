package com.codepath.apps.restclienttemplate.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.DaoMaster;
import com.codepath.apps.restclienttemplate.dao.DaoSession;
import com.codepath.apps.restclienttemplate.dao.DirectMessage;
import com.codepath.apps.restclienttemplate.dao.DirectMessageDao;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonaswu on 2015/8/20.
 */
public abstract class OAuthBaseActivity extends OAuthLoginActionBarActivity<TwitterRestClient> {

    private TwitterDao twitterDao;
    private UserDao userDao;
    private CurrentUserDao currentUserDao;
    private MediaDao mediaDao;
    private DirectMessageDao directMessageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDb();
    }

    protected void initDb() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "greendao", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        twitterDao = daoSession.getTwitterDao();
        userDao = daoSession.getUserDao();
        currentUserDao = daoSession.getCurrentUserDao();
        mediaDao = daoSession.getMediaDao();
        directMessageDao = daoSession.getDirectMessageDao();
    }

    public void getUserCredential() {
        TwitterRestClient client = RestApplication.getRestClient();
        client.getcredentials(
                new MyJsonHttpResponseHandler(this) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        try {
                            JSONObject userJSON = (JSONObject) data;
                            Utils.setCurrentUserId(userJSON.getLong("id"));
                            Utils.setCurrentUserScreenName(userJSON.getString("screen_name"));
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            CurrentUser currentUser = gson.fromJson(userJSON.toString(), CurrentUser.class);
                            currentUserDao.insert(currentUser);
                            onGetUserSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorCallBack() {
                    }
                }
        );
    }

    public void onGetUserSuccess() {
        directMessageDao.deleteAll();
        TwitterRestClient directMessageClient = RestApplication.getRestClient();
        directMessageClient.getDirectMessages(
                new MyJsonHttpResponseHandler(this) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        JSONArray messageJSON = (JSONArray) data;
                        for (int i = 0; i < messageJSON.length(); i++) {
                            try {
                                JSONObject rawMessageJSON = messageJSON.getJSONObject(i);
                                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                                DirectMessage directMessage = gson.fromJson(rawMessageJSON.toString(), DirectMessage.class);
                                directMessageDao.insert(directMessage);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        onGetSuccess();
                    }

                    @Override
                    public void errorCallBack() {
                    }
                }
        );
    }

    public void onGetSuccess() {
    }
}
