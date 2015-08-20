package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.fragments.UserFragment;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/19.
 */
public class ProfileActivity extends BaseActivity implements TweetFragment.PostSuccessDelegator {
    private long userId;
    private String screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getLong("userId");
            screenName = bundle.getString("screenName");
        } else {
            userId = Utils.getCurrentUserId();
            screenName = Utils.getCurrentUserScreenName();
        }
        getUser(userId, screenName);
    }

    private void createView() {

        Query query = userDao.queryBuilder().where(
                UserDao.Properties.Id.eq(userId),
                UserDao.Properties.Screen_name.eq(screenName)).build();
        final User user = (User) query.list().get(0);

        TextView favoriteText = (TextView) findViewById(R.id.favorite);
        TextView followersText = (TextView) findViewById(R.id.followers);
        TextView tweetCounts = (TextView) findViewById(R.id.tweets);
        TextView screenname = (TextView) findViewById(R.id.screenname);
        TextView name = (TextView) findViewById(R.id.name);
        ImageView profile_image = (ImageView) findViewById(R.id.profile_image);
        ImageView profile_background_image = (ImageView) findViewById(R.id.profile_background_image);

        favoriteText.setText(String.valueOf(user.getFavourites_count()));
        followersText.setText(String.valueOf(user.getFollowers_count()));
        tweetCounts.setText(String.valueOf(user.getListed_count()));

        name.setText(Html.fromHtml(user.getName()));
        screenname.setText("@" + user.getScreen_name());
        Picasso.with(ProfileActivity.this)
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(profile_image);

        Picasso.with(ProfileActivity.this)
                .load(user.getProfile_background_image_url())
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(profile_background_image);


        LinearLayout linearlayout = (LinearLayout) findViewById(R.id.container);
        FragmentManager fragMan = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragMan.beginTransaction();

        fragTransaction.add(linearlayout.getId(), UserFragment.newInstance(userId, screenName), "frag0");
        fragTransaction.commit();

    }

    private void getUser(Long userId, String screenName) {
        TwitterRestClient client = RestApplication.getRestClient();
        client.getUser(userId, screenName,
                new MyJsonHttpResponseHandler(this) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        JSONObject userJSON = (JSONObject) data;
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                        User user = gson.fromJson(userJSON.toString(), User.class);
                        userDao.insert(user);
                        createView();
                    }

                    @Override
                    public void errorCallBack() {
                    }
                }
        );
    }

    @Override
    public void postSuccess(SaveDataToDB saveDataToDB) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.findItem(R.id.user);
        item.setVisible(false);
        return true;
    }
}
