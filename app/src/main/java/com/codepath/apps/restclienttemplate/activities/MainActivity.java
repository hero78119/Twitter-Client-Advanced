package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.adapters.MyFragmentPagerAdapter;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.interfaces.Reloadable;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;

import java.util.HashMap;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/9.
 */

public class MainActivity extends BaseActivity implements TweetFragment.PostSuccessDelegator {

    private SmartTabLayout viewPagerTab;
    private ViewPager viewPager;
    private MyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        adapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager(),
                this
        );
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setCustomTabView(adapter);
        viewPagerTab.setViewPager(viewPager);

    }

    @Override
    public void postSuccess(SaveDataToDB saveDataToDB) {
        int currentPos = viewPager.getCurrentItem();
        ((Reloadable) (adapter.getFragment(currentPos))).reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        super.onCreateOptionsMenu(menu);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchItem.collapseActionView();
                // setCurrentQueryString(s);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("searchText", s);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("onQueryTextChange", s);
                return false;
            }

        });
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackgroundResource(R.color.white);
            TextView searchText = (TextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            if (searchText != null) {
                searchText.setHintTextColor(Color.GRAY);
                searchText.setTextColor(Color.BLACK);
            }
        }
        return true;
    }

}