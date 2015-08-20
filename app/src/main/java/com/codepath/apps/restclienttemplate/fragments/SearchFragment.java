package com.codepath.apps.restclienttemplate.fragments;

import android.content.Intent;
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
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.interfaces.Reloadable;
import com.codepath.apps.restclienttemplate.lib.EndlessScrollListener;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jonaswu on 2015/8/20.
 */
public class SearchFragment extends BaseTimelineFragment {

    private SwipeRefreshLayout swipeContainer;
    private ListView mainListView;
    private String searchText;

    public static Fragment newInstance(String searchText) {
        SearchFragment fragment = new SearchFragment();
        fragment.setSearchText(searchText);
        return fragment;
    }

    @Override
    public SwipeRefreshLayout getSwipeContainer() {
        return swipeContainer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.fetchOnRefresh = true;

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setColorSchemeColors(0, 0, 0, 0);
        swipeContainer.setProgressBackgroundColor(android.R.color.transparent);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SearchFragment.this.initData(true);
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
        client.search(getSearchText(), maxId,
                new MyJsonHttpResponseHandler(getActivity()) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        Log.e("data", ((JSONObject) data).toString());
                        try {
                            processDataToDB(maxId == null ? true : false, ((JSONObject) data).getJSONArray("statuses"), maxId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setFinish();
                    }

                    @Override
                    public void errorCallBack() {
                        setFinish();
                    }
                }
        );
    }


    @Override
    public String getTweetType() {
        return "search";
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
