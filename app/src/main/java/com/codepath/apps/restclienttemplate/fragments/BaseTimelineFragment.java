package com.codepath.apps.restclienttemplate.fragments;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.adapters.MainListAdapter;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.DaoMaster;
import com.codepath.apps.restclienttemplate.dao.DaoSession;
import com.codepath.apps.restclienttemplate.dao.Media;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.interfaces.Progressable;
import com.codepath.apps.restclienttemplate.interfaces.Reloadable;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import de.greenrobot.dao.query.Query;

public abstract class BaseTimelineFragment extends Fragment implements Reloadable, Progressable {
    public static final String ARG_PAGE = "ARG_PAGE";

    protected TwitterDao twitterDao;
    protected boolean fetchOnRefresh = true;
    protected UserDao userDao;
    protected MainListAdapter adapter;
    private MediaDao mediaDao;
    private int mPage;
    private CurrentUserDao currentUserDao;
    protected DaoSession daoSession;
    private boolean reachToTheEnd = false;

    public static Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Fragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mPage = args.getInt(ARG_PAGE);
        }
        initDb();
    }

    public abstract SwipeRefreshLayout getSwipeContainer();

    private void initDb() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "greendao", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        twitterDao = daoSession.getTwitterDao();
        userDao = daoSession.getUserDao();
        mediaDao = daoSession.getMediaDao();
        currentUserDao = daoSession.getCurrentUserDao();

        adapter = new MainListAdapter(getActivity());

        initData(false);
    }


    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState);

    public abstract void fetchData(final Long maxId);

    public abstract String getTweetType();

    protected void initData(boolean forceRefresh) {
        // SomeActivity.java
        if (forceRefresh || fetchOnRefresh) {
            fetchData(null);
        } else {
            processDataToDB(true, null, null);
        }
    }

    protected void buildDelete() {
        twitterDao.queryBuilder().where(TwitterDao.Properties.Type.eq(getTweetType())
        ).buildDelete().executeDeleteWithoutDetachingEntities();
    }

    protected void processDataToDB(boolean clean, JSONArray data, Long lessThen) {
        if (data != null) {
            if (data.length() < 20) {
                setReachToTheEnd(true);
            } else {
                setReachToTheEnd(false);
            }
            if (clean) {
                daoSession.clear();
                adapter.deleteAll();
                buildDelete();
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject post = data.getJSONObject(i);
                    JSONObject userJSON = post.getJSONObject("user");
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    Twitter twitterPost = gson.fromJson(Utils.twitterPostPreProcessForDao(post).toString(), Twitter.class);

                    User user = gson.fromJson(userJSON.toString(), User.class);
                    userDao.insert(user);

                    // user already contain id after insert to db
                    twitterPost.setUser(user);

                    try {
                        if (post.getLong("in_reply_to_status_id") != 0) {
                            twitterPost.setIn_reply_to_user_id(post.getLong("in_reply_to_status_id"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    twitterPost.setType(getTweetType());

                    twitterDao.insert(twitterPost);

                    try {
                        JSONArray medias = post.getJSONObject("entities").getJSONArray("media");
                        for (int j = 0; j < medias.length(); j++) {
                            Media media = gson.fromJson(medias.getJSONObject(j).toString(), Media.class);
                            media.setInTweets(twitterPost.getInternalId());
                            mediaDao.insert(media);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        updateListView(lessThen);

    }

    private void updateListView(Long lessThen) {
        List<Twitter> list = filterTwitterItemToAddToView(lessThen);
        for (Twitter twitter : list) {
            adapter.addItem(twitter);
        }
        adapter.notifyDataSetChanged();
    }

    public List<Twitter> filterTwitterItemToAddToView(Long lessThen) {
        Query query;
        if (lessThen != null) {
            query = twitterDao.queryBuilder().where(
                    TwitterDao.Properties.Id.lt(lessThen),
                    TwitterDao.Properties.Type.eq(getTweetType())
            )
                    .build();
        } else {
            query = twitterDao.queryBuilder().where(TwitterDao.Properties.Type.eq(getTweetType()))
                    .build();
        }
        List<Twitter> list = query.list();
        return list;
    }

    @Override
    public void reload() {
        initData(true);
    }

    public boolean isReachToTheEnd() {
        return reachToTheEnd;
    }

    public void setReachToTheEnd(boolean reachToTheEnd) {
        this.reachToTheEnd = reachToTheEnd;
    }


    public void setBusy() {
        try {
            ((Progressable) getActivity()).setBusy();
        } catch (Exception e) {

        }
    }

    public void setFinish() {
        try {
            ((Progressable) getActivity()).setFinish();
        } catch (Exception e) {

        }
    }
}
