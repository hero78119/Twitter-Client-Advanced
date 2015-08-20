package com.codepath.apps.restclienttemplate.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.fragments.SearchFragment;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;

/**
 * Created by jonaswu on 2015/8/20.
 */
public class SearchActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If not already added to the Fragment manager add it. If you don't do this a new Fragment will be added every time this method is called (Such as on orientation change)
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, SearchFragment.newInstance(getIntent().getExtras().getString("searchText")), "frag").commit();
    }

    @Override
    public void postSuccess(SaveDataToDB saveDataToDB) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        menu.findItem(R.id.write).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.findItem(R.id.user).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                setSupportProgressBarIndeterminateVisibility(true);
                searchItem.collapseActionView();
                SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("frag");
                searchFragment.setSearchText(s);
                searchFragment.reload();
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
