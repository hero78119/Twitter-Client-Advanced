package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.activities.DetailActivity;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.fragments.HomeFragment;
import com.codepath.apps.restclienttemplate.fragments.MentionsFragment;
import com.codepath.apps.restclienttemplate.lib.EndlessScrollListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonaswu on 2015/8/18.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter implements SmartTabLayout.TabProvider {
    final int PAGE_COUNT = 2;
    private final FragmentManager fm;
    private String tabTitles[] = new String[]{"HOME", "MENTIONS"};
    private Map<Integer, Fragment> mFragmentTags = new HashMap<>();

    private final Context context;

    public MyFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container, position);
        if (obj instanceof Fragment) {
            // record the fragment tag here.
            switch (position) {
                case 0:
                    mFragmentTags.put(0, (Fragment) obj);
                    break;
                case 1:
                    mFragmentTags.put(1, (Fragment) obj);
                    break;
            }
        }
        return obj;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag;
        switch (position) {
            case 0:
                frag = HomeFragment.newInstance(position);
                break;
            case 1:
                frag = MentionsFragment.newInstance(position);
                break;
            default:
                frag = HomeFragment.newInstance(position);
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    @Override
    public View createTabView(ViewGroup viewGroup, int i, PagerAdapter pagerAdapter) {
        Log.e("createTabView", "createTabView");
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tab_layout, viewGroup, false);
        TextView text = (TextView) view.findViewById(R.id.text);
        ImageView image = (ImageView) view.findViewById(R.id.image);
        text.setText(tabTitles[i]);
        if (i == 0) {
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.home153));
        } else if (i == 1) {
            image.setImageDrawable(context.getResources().getDrawable(R.drawable.light));

        }
        return view;
    }

    public Fragment getFragment(int position) {
        return mFragmentTags.get(position);
    }
}
