package com.codepath.apps.restclienttemplate.activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.DaoMaster;
import com.codepath.apps.restclienttemplate.dao.DaoSession;
import com.codepath.apps.restclienttemplate.dao.DirectMessage;
import com.codepath.apps.restclienttemplate.dao.DirectMessageDao;
import com.codepath.apps.restclienttemplate.dao.Media;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.interfaces.Progressable;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/20.
 */
public abstract class BaseActivity extends AppCompatActivity implements TweetFragment.PostSuccessDelegator, Progressable {
    protected TwitterDao twitterDao;
    protected UserDao userDao;
    protected CurrentUserDao currentUserDao;
    protected MediaDao mediaDao;
    protected MenuItem menuItem;
    private DirectMessageDao directMessageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        initNavView();
    }

    public void initNavView() {
        Query query = userDao.queryBuilder().where(
                UserDao.Properties.Id.eq(Utils.getCurrentUserId()),
                UserDao.Properties.Screen_name.eq(Utils.getCurrentUserScreenName())).build();
        List<User> list = (List<User>) query.list();
        if (list.size() > 0) {
            final User user = list.get(0);

            Query directMessageQuery = directMessageDao.queryBuilder().build();
            List<DirectMessage> directMessages = (List<DirectMessage>) directMessageQuery.list();


            DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {

                @Override
                public void set(ImageView imageView, Uri uri, Drawable drawable) {
                    Picasso.with(BaseActivity.this)
                            .load(user.getProfile_image_url())
                            .transform(new RoundedTransformation(15, 1))
                            .error(R.drawable.images)
                            .placeholder(R.drawable.placeholder)
                            .centerInside()
                            .noFade()
                            .fit()
                            .into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Picasso.with(imageView.getContext()).cancelRequest(imageView);
                }

                @Override
                public Drawable placeholder(Context context) {
                    return null;
                }

            });
            // Create the AccountHeader
            AccountHeader headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .addProfiles(
                            new ProfileDrawerItem().withName(user.getName()).withEmail("@" + user.getScreen_name()).withIcon(user.getProfile_image_url())
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                            return false;
                        }
                    })
                    .build();

            Picasso.with(this)
                    .load(user.getProfile_background_image_url())
                    .error(R.drawable.images)
                    .placeholder(R.drawable.placeholder)
                    .centerInside()
                    .noFade()
                    .fit()
                    .into(headerResult.getHeaderBackgroundView());

            ArrayList<IDrawerItem> items = new ArrayList<IDrawerItem>();
            for (DirectMessage directMessage : directMessages) {
                items.add(new PrimaryDrawerItem().withName("@" + directMessage.getSender_screen_name() + "   " + directMessage.getText()));
            }
            IDrawerItem[] itemsArgs = items.toArray(new IDrawerItem[]{});
            DrawerBuilder buider = new DrawerBuilder()
                    .withActivity(this)
                    .withAccountHeader(headerResult)
                    .withTranslucentStatusBar(false)
                    .withActionBarDrawerToggle(false)
                    .addDrawerItems(itemsArgs);
            buider.build();
        }
    }

    public void setBusy() {
        if (menuItem != null) {
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menuItem.setVisible(true);
        }
    }

    public void setFinish() {
        if (menuItem != null) {
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            menuItem.setVisible(false);
        }
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
                                DirectMessage currentUser = gson.fromJson(rawMessageJSON.toString(), DirectMessage.class);
                                directMessageDao.insert(currentUser);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.write) {
            showPostDialog();
            return true;
        }

        if (id == R.id.logout) {
            TwitterRestClient client = RestApplication.getRestClient();
            client.clearAccessToken();
            finish();
            return true;
        }


        if (id == R.id.user) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.progress) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.progress);
        menuItem.setVisible(false);
        return true;
    }


    private void showPostDialog() {
        if (Utils.getCurrentUserId() != null) {
            FragmentManager fm = getSupportFragmentManager();
            Query query = currentUserDao.queryBuilder().where(UserDao.Properties.Id.eq(Utils.getCurrentUserId())).build();
            List<CurrentUser> currentUsers = query.list();
            if (currentUsers.size() > 0) {
                CurrentUser user = (CurrentUser) query.list().get(0);
                TweetFragment alertDialog = TweetFragment.newInstanceAsPostNewTweet(user, this);
                alertDialog.show(fm, "filter");
            }
        } else {
            getUserCredential();
        }
    }

    public void showReplyDialog(Long replyTo) {
        FragmentManager fm = getSupportFragmentManager();
        Query query = currentUserDao.queryBuilder().where(UserDao.Properties.Id.eq(Utils.getCurrentUserId())).build();
        List<CurrentUser> currentUsers = query.list();
        if (currentUsers.size() > 0) {
            CurrentUser user = (CurrentUser) query.list().get(0);
            TweetFragment alertDialog = TweetFragment.newInstanceAsReply(user, this, replyTo);
            alertDialog.show(fm, "filter");
        }

    }

    public abstract void postSuccess(SaveDataToDB saveDataToDB);
}
