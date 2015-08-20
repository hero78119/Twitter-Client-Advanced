package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.activities.DetailActivity;
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
import com.codepath.apps.restclienttemplate.interfaces.Reloadable;
import com.codepath.apps.restclienttemplate.lib.EndlessScrollListener;
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

/**
 * Created by jonaswu on 2015/8/18.
 */
public class MentionsFragment extends BaseTimelineFragment {
    private SwipeRefreshLayout swipeContainer;
    private ListView mainListView;

    public static Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Fragment fragment = new MentionsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public SwipeRefreshLayout getSwipeContainer() {
        return swipeContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.fetchOnRefresh = true;
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeColors(0, 0, 0, 0);
        swipeContainer.setProgressBackgroundColor(android.R.color.transparent);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MentionsFragment.this.initData(true);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mainListView = (ListView) view.findViewById(R.id.mainlist);

        mainListView.setAdapter(adapter);

        mainListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.e("isReachToTheEnd()", String.valueOf(isReachToTheEnd()));
                if (!isReachToTheEnd()) {
                    Twitter lastItem = (Twitter) adapter.getLastItem();
                    Long maxId = lastItem.getId();
                    fetchData(maxId);
                }
            }
        });

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Twitter twitter = (Twitter) adapter.getItem(position);
                Intent intent = new Intent(getActivity()
                        , DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", twitter.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void fetchData(final Long maxId) {
        setBusy();
        TwitterRestClient client = RestApplication.getRestClient();
        client.getMentionTimeline(maxId,
                new MyJsonHttpResponseHandler(getActivity()) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        swipeContainer.setRefreshing(false);
                        processDataToDB(maxId == null ? true : false, (JSONArray) data, maxId);
                        setFinish();
                    }

                    @Override
                    public void errorCallBack() {
                        setFinish();
                        swipeContainer.setRefreshing(false
                        );
                    }
                }
        );

    }

    @Override
    public String getTweetType() {
        return "mentions";
    }

}
